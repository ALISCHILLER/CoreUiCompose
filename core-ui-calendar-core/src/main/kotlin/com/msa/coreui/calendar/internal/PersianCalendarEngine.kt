/*
 * Portions of this conversion engine are a Kotlin adaptation of jalaali-js.
 * Copyright (c) 2020 Behrang Norouzinia.
 * Licensed under the MIT License; see THIRD_PARTY_NOTICES.md.
 */
package com.msa.coreui.calendar.internal

import com.msa.coreui.calendar.model.PersianDate
import java.time.LocalDate

/**
 * Pure, deterministic Solar Hijri conversion and validation engine.
 *
 * The implementation uses Borkowski's break-year algorithm and Julian day numbers. It has no
 * Android or locale dependency, which keeps conversion logic deterministic, unit-testable and safe
 * to use from any thread.
 */
internal object PersianCalendarEngine {

    const val MIN_SUPPORTED_YEAR: Int = -61
    const val MAX_SUPPORTED_YEAR: Int = 3176

    val MIN_SUPPORTED_GREGORIAN_DATE: LocalDate = LocalDate.of(560, 3, 20)
    val MAX_SUPPORTED_GREGORIAN_DATE: LocalDate = LocalDate.of(3798, 3, 19)

    private val breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
        1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178,
    )
    private const val MAX_CALCULATION_YEAR: Int = 3177

    fun fromGregorian(date: LocalDate): PersianDate {
        require(date in MIN_SUPPORTED_GREGORIAN_DATE..MAX_SUPPORTED_GREGORIAN_DATE) {
            "Gregorian date $date is outside the supported range " +
                "$MIN_SUPPORTED_GREGORIAN_DATE..$MAX_SUPPORTED_GREGORIAN_DATE."
        }
        val dayNumber = gregorianToDayNumber(date.year, date.monthValue, date.dayOfMonth)
        val result = dayNumberToPersian(dayNumber)
        return PersianDate(result.year, result.month, result.day)
    }

    fun toGregorian(date: PersianDate): LocalDate {
        val dayNumber = persianToDayNumber(date.year, date.month, date.day)
        val result = dayNumberToGregorian(dayNumber)
        return LocalDate.of(result.year, result.month, result.day)
    }

    fun isLeapYear(year: Int): Boolean {
        require(year in MIN_SUPPORTED_YEAR..MAX_SUPPORTED_YEAR) { "Unsupported Persian year: $year" }
        return jalaliCalendar(year).leap == 0
    }

    fun lengthOfMonth(year: Int, month: Int): Int {
        require(year in MIN_SUPPORTED_YEAR..MAX_SUPPORTED_YEAR) { "Unsupported Persian year: $year" }
        require(month in 1..12) { "Persian month must be between 1 and 12." }
        return when {
            month <= 6 -> 31
            month <= 11 -> 30
            isLeapYear(year) -> 30
            else -> 29
        }
    }

    fun isValid(year: Int, month: Int, day: Int): Boolean {
        if (year !in MIN_SUPPORTED_YEAR..MAX_SUPPORTED_YEAR || month !in 1..12) return false
        return day in 1..lengthOfMonth(year, month)
    }

    private fun persianToDayNumber(year: Int, month: Int, day: Int): Int {
        require(isValid(year, month, day)) { "Invalid Persian date: $year/$month/$day" }
        val calendar = jalaliCalendar(year)
        return gregorianToDayNumber(calendar.gregorianYear, 3, calendar.marchDay) +
            (month - 1) * 31 - divide(month, 7) * (month - 7) + day - 1
    }

    private fun dayNumberToPersian(dayNumber: Int): DateParts {
        val gregorian = dayNumberToGregorian(dayNumber)
        var year = gregorian.year - 621
        val calendar = jalaliCalendar(year)
        val firstFarvardin = gregorianToDayNumber(gregorian.year, 3, calendar.marchDay)
        var offset = dayNumber - firstFarvardin

        if (offset >= 0) {
            if (offset <= 185) {
                return DateParts(
                    year = year,
                    month = 1 + divide(offset, 31),
                    day = remainder(offset, 31) + 1,
                )
            }
            offset -= 186
        } else {
            year -= 1
            offset += 179
            if (jalaliCalendar(year).leap == 0) offset += 1
        }

        return DateParts(
            year = year,
            month = 7 + divide(offset, 30),
            day = remainder(offset, 30) + 1,
        )
    }

    private fun jalaliCalendar(year: Int): JalaliCalendarResult {
        require(year in MIN_SUPPORTED_YEAR..MAX_CALCULATION_YEAR) {
            "Unsupported Persian calculation year: $year"
        }

        val gregorianYear = year + 621
        var leapJalali = -14
        var previousBreak = breaks.first()
        var jump = 0
        for (index in 1 until breaks.size) {
            val currentBreak = breaks[index]
            jump = currentBreak - previousBreak
            if (year < currentBreak) break
            leapJalali += divide(jump, 33) * 8 + divide(remainder(jump, 33), 4)
            previousBreak = currentBreak
        }

        var yearsSinceBreak = year - previousBreak
        leapJalali += divide(yearsSinceBreak, 33) * 8 +
            divide(remainder(yearsSinceBreak, 33) + 3, 4)

        if (remainder(jump, 33) == 4 && jump - yearsSinceBreak == 4) {
            leapJalali += 1
        }

        val leapGregorian = divide(gregorianYear, 4) -
            divide((divide(gregorianYear, 100) + 1) * 3, 4) - 150
        val marchDay = 20 + leapJalali - leapGregorian

        if (jump - yearsSinceBreak < 6) {
            yearsSinceBreak = yearsSinceBreak - jump + divide(jump + 4, 33) * 33
        }

        var leap = remainder(remainder(yearsSinceBreak + 1, 33) - 1, 4)
        if (leap == -1) leap = 4

        return JalaliCalendarResult(
            leap = leap,
            gregorianYear = gregorianYear,
            marchDay = marchDay,
        )
    }

    private fun gregorianToDayNumber(year: Int, month: Int, day: Int): Int {
        var result = divide(
            (year + divide(month - 8, 6) + 100_100) * 1461,
            4,
        )
        result += divide(153 * remainder(month + 9, 12) + 2, 5)
        result += day - 34_840_408
        result -= divide(
            divide(year + 100_100 + divide(month - 8, 6), 100) * 3,
            4,
        )
        return result + 752
    }

    private fun dayNumberToGregorian(dayNumber: Int): DateParts {
        var value = 4 * dayNumber + 139_361_631
        value += divide(divide(4 * dayNumber + 183_187_720, 146_097) * 3, 4) * 4 - 3908
        val intermediate = divide(remainder(value, 1461), 4) * 5 + 308
        val day = divide(remainder(intermediate, 153), 5) + 1
        val month = remainder(divide(intermediate, 153), 12) + 1
        val year = divide(value, 1461) - 100_100 + divide(8 - month, 6)
        return DateParts(year, month, day)
    }

    private fun divide(value: Int, divisor: Int): Int = value / divisor

    private fun remainder(value: Int, divisor: Int): Int = value % divisor

    private data class JalaliCalendarResult(
        val leap: Int,
        val gregorianYear: Int,
        val marchDay: Int,
    )

    private data class DateParts(
        val year: Int,
        val month: Int,
        val day: Int,
    )
}
