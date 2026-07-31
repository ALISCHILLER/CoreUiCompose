package com.msa.coreui.calendar.model

/** True when the selection contains no selected date. */
public val PersianDateSelection.isEmpty: Boolean
    get() = when (this) {
        is PersianDateSelection.Single -> date == null
        is PersianDateSelection.Range -> start == null && end == null
        is PersianDateSelection.Multiple -> dates.isEmpty()
    }

/** True when the selection satisfies the minimum confirmation contract for its mode. */
public val PersianDateSelection.isComplete: Boolean
    get() = when (this) {
        is PersianDateSelection.Single -> date != null
        is PersianDateSelection.Range -> start != null && end != null
        is PersianDateSelection.Multiple -> dates.isNotEmpty()
    }

/** Number of explicit selected endpoints/dates, without expanding a date range. */
public val PersianDateSelection.selectedDateCount: Int
    get() = when (this) {
        is PersianDateSelection.Single -> if (date == null) 0 else 1
        is PersianDateSelection.Range -> when {
            start == null -> 0
            end == null || end == start -> 1
            else -> 2
        }
        is PersianDateSelection.Multiple -> dates.size
    }

/** Returns the normalized inclusive range only when both range endpoints are present. */
public fun PersianDateSelection.Range.toGregorianDateRangeOrNull(): GregorianDateRange? {
    val first = start ?: return null
    val last = end ?: return null
    return GregorianDateRange(minOf(first, last), maxOf(first, last))
}
