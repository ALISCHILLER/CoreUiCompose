package com.msa.coreui.calendar.model

import java.io.Serializable
import java.time.LocalDate

/**
 * Immutable inclusive Gregorian date range.
 *
 * Core UI intentionally uses [LocalDate] at public boundaries so values remain independent from
 * locale, numeral style, device time zone, and UI formatting.
 */
public data class GregorianDateRange(
    public override val start: LocalDate,
    public override val endInclusive: LocalDate,
) : ClosedRange<LocalDate>, Serializable {

    init {
        require(!start.isAfter(endInclusive)) {
            "start must be on or before endInclusive."
        }
    }

    public val dayCount: Long
        get() = endInclusive.toEpochDay() - start.toEpochDay() + 1L

    public fun overlaps(other: GregorianDateRange): Boolean =
        start <= other.endInclusive && endInclusive >= other.start

    public fun isAdjacentTo(other: GregorianDateRange): Boolean =
        endInclusive.toEpochDay() + 1L == other.start.toEpochDay() ||
            other.endInclusive.toEpochDay() + 1L == start.toEpochDay()

    public fun merge(other: GregorianDateRange): GregorianDateRange {
        require(overlaps(other) || isAdjacentTo(other)) {
            "Only overlapping or adjacent ranges can be merged."
        }
        return GregorianDateRange(
            start = minOf(start, other.start),
            endInclusive = maxOf(endInclusive, other.endInclusive),
        )
    }
}
