package com.msa.coreui.calendar.model

import java.time.LocalDate
import java.util.Collections
import java.util.TreeSet

/** Supported selection behaviors for the Persian date picker. */
public enum class PersianDateSelectionMode {
    SINGLE,
    RANGE,
    MULTIPLE,
}

/** Immutable result returned by the date picker. All values are Gregorian [LocalDate] instances. */
public sealed interface PersianDateSelection {

    public val mode: PersianDateSelectionMode

    public data class Single(public val date: LocalDate?) : PersianDateSelection {
        public override val mode: PersianDateSelectionMode = PersianDateSelectionMode.SINGLE
    }

    public data class Range(
        public val start: LocalDate?,
        public val end: LocalDate?,
    ) : PersianDateSelection {
        public override val mode: PersianDateSelectionMode = PersianDateSelectionMode.RANGE
    }

    public class Multiple(dates: Set<LocalDate>) : PersianDateSelection {
        public override val mode: PersianDateSelectionMode = PersianDateSelectionMode.MULTIPLE
        public val dates: Set<LocalDate> = Collections.unmodifiableSet(TreeSet(dates))

        public override fun equals(other: Any?): Boolean =
            this === other || other is Multiple && dates == other.dates

        public override fun hashCode(): Int = dates.hashCode()

        public override fun toString(): String = "Multiple(dates=$dates)"
    }

    public companion object {
        public fun empty(mode: PersianDateSelectionMode): PersianDateSelection = when (mode) {
            PersianDateSelectionMode.SINGLE -> Single(null)
            PersianDateSelectionMode.RANGE -> Range(null, null)
            PersianDateSelectionMode.MULTIPLE -> Multiple(emptySet())
        }
    }
}
