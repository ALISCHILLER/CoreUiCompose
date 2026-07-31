#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

for command_name in kotlinc java; do
  if ! command -v "$command_name" >/dev/null 2>&1; then
    printf 'Standalone calendar verification requires %s on PATH.\n' "$command_name" >&2
    exit 2
  fi
done

TEMP_DIR="$(mktemp -d "${TMPDIR:-/tmp}/msa-calendar-verification.XXXXXX")"
trap 'rm -rf "$TEMP_DIR"' EXIT
export TERM="${TERM:-dumb}"

mkdir -p \
  "$TEMP_DIR/stubs/org/junit" \
  "$TEMP_DIR/stubs/androidx/compose/runtime/saveable" \
  "$TEMP_DIR/stubs/androidx/compose/runtime" \
  "$TEMP_DIR/stubs/androidx/compose/ui/platform" \
  "$TEMP_DIR/stubs/androidx/compose/ui/unit" \
  "$TEMP_DIR/stubs/com/msa/coreui/calendar"

cat > "$TEMP_DIR/stubs/org/junit/Test.kt" <<'KOTLIN'
package org.junit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Test
KOTLIN

cat > "$TEMP_DIR/stubs/org/junit/Assert.kt" <<'KOTLIN'
package org.junit

object Assert {
    @JvmStatic
    fun assertTrue(condition: Boolean) {
        if (!condition) throw AssertionError("Expected true")
    }

    @JvmStatic
    fun assertTrue(message: String, condition: Boolean) {
        if (!condition) throw AssertionError(message)
    }

    @JvmStatic
    fun assertFalse(condition: Boolean) {
        if (condition) throw AssertionError("Expected false")
    }

    @JvmStatic
    fun assertFalse(message: String, condition: Boolean) {
        if (condition) throw AssertionError(message)
    }

    @JvmStatic
    fun assertEquals(expected: Any?, actual: Any?) {
        if (expected != actual) {
            throw AssertionError("Expected <$expected> but was <$actual>")
        }
    }

    @JvmStatic
    fun assertEquals(message: String, expected: Any?, actual: Any?) {
        if (expected != actual) {
            throw AssertionError("$message: expected <$expected> but was <$actual>")
        }
    }

    @JvmStatic
    fun <T : Throwable> assertThrows(expectedType: Class<T>, block: () -> Unit): T {
        try {
            block()
        } catch (throwable: Throwable) {
            if (expectedType.isInstance(throwable)) return expectedType.cast(throwable)
            throw AssertionError(
                "Expected ${expectedType.name} but caught ${throwable::class.java.name}",
                throwable,
            )
        }
        throw AssertionError("Expected ${expectedType.name} to be thrown")
    }
}
KOTLIN

cat > "$TEMP_DIR/stubs/androidx/compose/runtime/Runtime.kt" <<'KOTLIN'
package androidx.compose.runtime

import kotlin.reflect.KProperty

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class Composable

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class Stable

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Immutable

class MutableState<T>(var value: T)

fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)

operator fun <T> MutableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

operator fun <T> MutableState<T>.setValue(
    thisRef: Any?,
    property: KProperty<*>,
    value: T,
) {
    this.value = value
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun <T> remember(vararg keys: Any?, calculation: () -> T): T = calculation()
KOTLIN

cat > "$TEMP_DIR/stubs/androidx/compose/runtime/saveable/Saveable.kt" <<'KOTLIN'
package androidx.compose.runtime.saveable

import androidx.compose.runtime.Composable

class Saver<Original, Saveable>(
    val save: (Original) -> Saveable?,
    val restore: (Saveable) -> Original?,
)

fun <T> mapSaver(
    save: (T) -> Map<String, Any?>,
    restore: (Map<String, Any?>) -> T?,
): Saver<T, Any> = Saver(
    save = { value -> save(value) },
    restore = { value ->
        @Suppress("UNCHECKED_CAST")
        restore(value as Map<String, Any?>)
    },
)

@Composable
@Suppress("UNUSED_PARAMETER")
fun <T> rememberSaveable(
    vararg inputs: Any?,
    saver: Saver<T, Any>,
    init: () -> T,
): T = init()
KOTLIN

cat > "$TEMP_DIR/stubs/androidx/compose/ui/unit/Dp.kt" <<'KOTLIN'
package androidx.compose.ui.unit

@JvmInline
value class Dp(val value: Float) : Comparable<Dp> {
    override fun compareTo(other: Dp): Int = value.compareTo(other.value)
    override fun toString(): String = "${value}.dp"
}

val Int.dp: Dp get() = Dp(toFloat())
val Float.dp: Dp get() = Dp(this)
val Double.dp: Dp get() = Dp(toFloat())
KOTLIN

cat > "$TEMP_DIR/stubs/androidx/compose/ui/platform/LocalConfiguration.kt" <<'KOTLIN'
package androidx.compose.ui.platform

class StubLocale(val language: String)
class StubLocaleList(private val values: List<StubLocale?>) {
    operator fun get(index: Int): StubLocale? = values[index]
}
class StubConfiguration(val locales: StubLocaleList)
object LocalConfiguration {
    val current: StubConfiguration = StubConfiguration(
        StubLocaleList(listOf(StubLocale("en"))),
    )
}
KOTLIN

cat > "$TEMP_DIR/stubs/com/msa/coreui/calendar/FormatSelection.kt" <<'KOTLIN'
package com.msa.coreui.calendar

import com.msa.coreui.calendar.model.PersianDateSelection

internal fun formatSelection(
    selection: PersianDateSelection,
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean = strings.defaultToPersianDigits,
): String = PersianDateSelectionFormatter.format(
    selection = selection,
    strings = strings,
    usePersianDigits = usePersianDigits,
)
KOTLIN

cat > "$TEMP_DIR/Runner.kt" <<'KOTLIN'
import java.lang.reflect.InvocationTargetException
import kotlin.system.exitProcess
import org.junit.Test

fun main(arguments: Array<String>) {
    var failures = 0
    var total = 0
    arguments.forEach { className ->
        val type = Class.forName(className)
        val instance = type.getDeclaredConstructor().newInstance()
        type.declaredMethods.sortedBy { method -> method.name }.forEach { method ->
            if (method.getAnnotation(Test::class.java) == null) return@forEach
            total += 1
            try {
                method.isAccessible = true
                method.invoke(instance)
                println("PASS $className.${method.name}")
            } catch (error: InvocationTargetException) {
                failures += 1
                val cause = error.targetException
                System.err.println(
                    "FAIL $className.${method.name}: ${cause::class.java.name}: ${cause.message}",
                )
                cause.printStackTrace()
            }
        }
    }
    println("RESULT total=$total failures=$failures")
    if (failures != 0) exitProcess(1)
}
KOTLIN

mapfile -t core_main < <(
  find core-ui-calendar-core/src/main/kotlin -type f -name '*.kt' -print | sort
)
mapfile -t core_tests < <(
  find core-ui-calendar-core/src/test/kotlin -type f -name '*.kt' -print | sort
)

printf 'Compiling calendar-core with strict explicit API and warnings as errors...\n'
kotlinc \
  -Xexplicit-api=strict \
  -Werror \
  "${core_main[@]}" \
  -d "$TEMP_DIR/calendar-core-main.jar"

printf 'Running calendar-core regression suite...\n'
kotlinc \
  "$TEMP_DIR/stubs/org/junit/Test.kt" \
  "$TEMP_DIR/stubs/org/junit/Assert.kt" \
  "${core_main[@]}" \
  "${core_tests[@]}" \
  "$TEMP_DIR/Runner.kt" \
  -include-runtime \
  -d "$TEMP_DIR/calendar-core-tests.jar"
java -jar "$TEMP_DIR/calendar-core-tests.jar" \
  com.msa.coreui.calendar.PersianCalendarEngineTest \
  com.msa.coreui.calendar.PersianCalendarPublicApiTest

calendar_main=(
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/PersianDatePickerConfig.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/PersianDatePickerDimensions.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/PersianDatePickerState.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/PersianDatePickerStrings.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/PersianDateSelectionFormatter.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/PersianDateSelectionResult.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/internal/PersianDateFormatting.kt
  core-ui-calendar/src/main/java/com/msa/coreui/calendar/internal/ui/PickerSelectionSummary.kt
)
mapfile -t calendar_tests < <(
  find core-ui-calendar/src/test/java -type f -name '*.kt' -print | sort
)
mapfile -t compose_stubs < <(
  find "$TEMP_DIR/stubs" -type f -name '*.kt' -print | sort
)

printf 'Running date-picker state, localization, and contract suite with minimal Compose type stubs...\n'
kotlinc \
  "${compose_stubs[@]}" \
  "${core_main[@]}" \
  "${calendar_main[@]}" \
  "${calendar_tests[@]}" \
  "$TEMP_DIR/Runner.kt" \
  -include-runtime \
  -d "$TEMP_DIR/calendar-state-tests.jar"
java -jar "$TEMP_DIR/calendar-state-tests.jar" \
  com.msa.coreui.calendar.PersianDateFormattingTest \
  com.msa.coreui.calendar.PersianDatePickerContractTest \
  com.msa.coreui.calendar.PersianDatePickerStateTest \
  com.msa.coreui.calendar.PersianDateConfirmationStatusTest \
  com.msa.coreui.calendar.PersianDateSelectionFormatterTest

printf 'Standalone calendar verification passed.\n'
