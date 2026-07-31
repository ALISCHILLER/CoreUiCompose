package com.msa.coreui.calendar

import com.msa.coreui.calendar.internal.PersianCalendarEngine
import com.msa.coreui.calendar.model.PersianDate
import java.time.LocalDate

/** Public, UI-independent entry point for Gregorian and Solar Hijri conversion. */
public object PersianCalendar {
    public const val MIN_SUPPORTED_YEAR: Int = PersianCalendarEngine.MIN_SUPPORTED_YEAR
    public const val MAX_SUPPORTED_YEAR: Int = PersianCalendarEngine.MAX_SUPPORTED_YEAR

    /** Earliest Gregorian date accepted by [fromGregorian]. */
    @JvmField
    public val MIN_SUPPORTED_GREGORIAN_DATE: LocalDate = PersianCalendarEngine.MIN_SUPPORTED_GREGORIAN_DATE

    /** Latest Gregorian date accepted by [fromGregorian]. */
    @JvmField
    public val MAX_SUPPORTED_GREGORIAN_DATE: LocalDate = PersianCalendarEngine.MAX_SUPPORTED_GREGORIAN_DATE

    @JvmStatic
    public fun fromGregorian(date: LocalDate): PersianDate = PersianCalendarEngine.fromGregorian(date)

    @JvmStatic
    public fun tryFromGregorian(date: LocalDate): PersianDate? = try {
        fromGregorian(date)
    } catch (_: IllegalArgumentException) {
        null
    }

    @JvmStatic
    public fun isSupported(date: LocalDate): Boolean = tryFromGregorian(date) != null

    @JvmStatic
    public fun toGregorian(date: PersianDate): LocalDate = PersianCalendarEngine.toGregorian(date)

    @JvmStatic
    public fun isLeapYear(year: Int): Boolean = PersianCalendarEngine.isLeapYear(year)

    @JvmStatic
    public fun lengthOfMonth(year: Int, month: Int): Int = PersianCalendarEngine.lengthOfMonth(year, month)

    @JvmStatic
    public fun isValid(year: Int, month: Int, day: Int): Boolean =
        PersianCalendarEngine.isValid(year, month, day)
}

/** Alternate naming for teams that prefer the term Jalali. */
public typealias JalaliDate = PersianDate
