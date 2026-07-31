package com.msa.coreui.calendar.internal

import com.msa.coreui.calendar.PersianDateDigitStyle
import com.msa.coreui.calendar.PersianDatePickerConfig
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.model.PersianDate
import java.time.DayOfWeek
import java.time.LocalDate

private val latinDigits = '0'..'9'
private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

internal fun String.withDigitStyle(usePersianDigits: Boolean): String {
    if (!usePersianDigits) return this
    return buildString(length) {
        this@withDigitStyle.forEach { character ->
            val index = latinDigits.indexOf(character)
            append(if (index >= 0) persianDigits[index] else character)
        }
    }
}

internal fun PersianDatePickerConfig.usePersianDigits(
    strings: PersianDatePickerStrings,
): Boolean = when (digitStyle) {
    PersianDateDigitStyle.AUTO -> strings.defaultToPersianDigits
    PersianDateDigitStyle.PERSIAN -> true
    PersianDateDigitStyle.LATIN -> false
}

internal fun PersianDate.format(
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean,
): String = "$day ${strings.monthNames[month - 1]} $year".withDigitStyle(usePersianDigits)

internal fun LocalDate.persianFormat(
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean,
): String = PersianDate.fromGregorian(this).format(strings, usePersianDigits)

/** Zero-based offset of this weekday from [firstDayOfWeek]. */
internal fun DayOfWeek.indexFrom(firstDayOfWeek: DayOfWeek): Int =
    Math.floorMod(value - firstDayOfWeek.value, 7)

/** Indices into a Saturday-first weekday label list, reordered for [firstDayOfWeek]. */
internal fun weekdayLabelOrder(firstDayOfWeek: DayOfWeek): List<Int> {
    val saturdayFirst = listOf(
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
    )
    val startIndex = saturdayFirst.indexOf(firstDayOfWeek)
    return List(7) { offset -> (startIndex + offset) % 7 }
}
