package com.msa.coreui.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.msa.coreui.calendar.internal.ui.DaysView
import com.msa.coreui.calendar.internal.ui.MonthsView
import com.msa.coreui.calendar.internal.ui.PickerHero
import com.msa.coreui.calendar.internal.ui.PickerNavigation
import com.msa.coreui.calendar.internal.ui.YearsView
import com.msa.coreui.calendar.internal.usePersianDigits
import com.msa.coreui.foundation.CoreUiTheme
import java.time.Clock
import java.time.LocalDate

/**
 * Material 3 Persian date picker supporting single, range and multiple selection.
 *
 * State is hoisted through [PersianDatePickerState]. The component inherits host layout direction
 * and exposes deterministic date callbacks without encoding Persian numerals in model values.
 */
@Composable
fun PersianDatePicker(
    state: PersianDatePickerState,
    modifier: Modifier = Modifier,
    strings: PersianDatePickerStrings = defaultPersianDatePickerStrings(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    dimensions: PersianDatePickerDimensions = PersianDatePickerDimensions(),
    clock: Clock = Clock.systemDefaultZone(),
    dateIndicatorCount: (LocalDate) -> Int = { 0 },
    dateAdditionalDescription: (LocalDate) -> String? = { null },
    onSelectionRejected: (PersianDateSelectionResult.Rejected) -> Unit = {},
) {
    val usePersianDigits = state.config.usePersianDigits(strings)
    val today = remember(clock) { LocalDate.now(clock) }
    val animationsEnabled = CoreUiTheme.motion.animationsEnabled
    val surfaceModifier = if (animationsEnabled) {
        modifier.animateContentSize(animationSpec = tween(CoreUiTheme.motion.normal))
    } else {
        modifier
    }

    Surface(
        modifier = surfaceModifier.testTag(PersianDatePickerTestTags.ROOT),
        color = colors.container,
        shape = androidx.compose.material3.MaterialTheme.shapes.extraLarge,
    ) {
        Column {
            PickerHero(
                state = state,
                strings = strings,
                colors = colors,
                usePersianDigits = usePersianDigits,
            )
            PickerNavigation(
                state = state,
                strings = strings,
                usePersianDigits = usePersianDigits,
            )
            if (animationsEnabled) {
                Crossfade(
                    targetState = state.view,
                    animationSpec = tween(CoreUiTheme.motion.normal),
                    label = "Persian date picker view",
                ) { view ->
                    PickerViewContent(
                        view = view,
                        state = state,
                        strings = strings,
                        colors = colors,
                        today = today,
                        usePersianDigits = usePersianDigits,
                        dateIndicatorCount = dateIndicatorCount,
                        dateAdditionalDescription = dateAdditionalDescription,
                        dimensions = dimensions,
                        onSelectionRejected = onSelectionRejected,
                    )
                }
            } else {
                PickerViewContent(
                    view = state.view,
                    state = state,
                    strings = strings,
                    colors = colors,
                    today = today,
                    usePersianDigits = usePersianDigits,
                    dateIndicatorCount = dateIndicatorCount,
                    dateAdditionalDescription = dateAdditionalDescription,
                    dimensions = dimensions,
                    onSelectionRejected = onSelectionRejected,
                )
            }
        }
    }
}

@Composable
private fun PickerViewContent(
    view: PersianDatePickerView,
    state: PersianDatePickerState,
    strings: PersianDatePickerStrings,
    colors: PersianDatePickerColors,
    today: LocalDate,
    usePersianDigits: Boolean,
    dateIndicatorCount: (LocalDate) -> Int,
    dateAdditionalDescription: (LocalDate) -> String?,
    dimensions: PersianDatePickerDimensions,
    onSelectionRejected: (PersianDateSelectionResult.Rejected) -> Unit,
) {
    when (view) {
        PersianDatePickerView.DAYS -> DaysView(
            state = state,
            strings = strings,
            colors = colors,
            today = today,
            usePersianDigits = usePersianDigits,
            dateIndicatorCount = dateIndicatorCount,
            dateAdditionalDescription = dateAdditionalDescription,
            dimensions = dimensions,
            onSelectionRejected = onSelectionRejected,
        )
        PersianDatePickerView.MONTHS -> MonthsView(
            state = state,
            strings = strings,
            dimensions = dimensions,
        )
        PersianDatePickerView.YEARS -> YearsView(
            state = state,
            strings = strings,
            usePersianDigits = usePersianDigits,
            dimensions = dimensions,
        )
    }
}
