package com.msa.coreui.calendar

import com.msa.coreui.calendar.model.PersianDate
import java.time.LocalDate

/** Stateless parsing and formatting helpers for Persian dates outside Compose UI. */
public object PersianDateFormatter {
    private val latinDigits: CharRange = '0'..'9'
    private val persianDigits: CharArray = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val arabicIndicDigits: CharArray = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    @JvmStatic
    @JvmOverloads
    public fun format(
        date: PersianDate,
        monthNames: List<String> = PersianCalendarText.PERSIAN_MONTH_NAMES,
        usePersianDigits: Boolean = true,
    ): String {
        require(monthNames.size == 12) { "monthNames must contain exactly 12 entries." }
        val result = "${date.day} ${monthNames[date.month - 1]} ${date.year}"
        return if (usePersianDigits) result.toPersianDigits() else result
    }

    @JvmStatic
    @JvmOverloads
    public fun formatGregorian(
        date: LocalDate,
        monthNames: List<String> = PersianCalendarText.PERSIAN_MONTH_NAMES,
        usePersianDigits: Boolean = true,
    ): String = format(PersianDate.fromGregorian(date), monthNames, usePersianDigits)

    /** Formats as a stable machine-readable Solar Hijri date, for example `1405-01-01`. */
    @JvmStatic
    public fun iso(date: PersianDate): String = date.toString()

    /** Formats a numeric date with caller-selected separator and numeral system. */
    @JvmStatic
    @JvmOverloads
    public fun numeric(
        date: PersianDate,
        separator: String = "/",
        usePersianDigits: Boolean = true,
    ): String {
        require(separator.isNotEmpty()) { "separator must not be empty." }
        val value = listOf(
            date.year.toString(),
            date.month.toString().padStart(2, '0'),
            date.day.toString().padStart(2, '0'),
        ).joinToString(separator)
        return if (usePersianDigits) value.toPersianDigits() else value
    }

    /** Parses strict ISO-like Solar Hijri text. Persian and Arabic-Indic digits are accepted. */
    @JvmStatic
    public fun parseIso(value: String): PersianDate = PersianDate.parseIso(value.toLatinDigits())

    @JvmStatic
    public fun tryParseIso(value: String): PersianDate? = try {
        parseIso(value)
    } catch (_: IllegalArgumentException) {
        null
    }

    @JvmStatic
    public fun String.toPersianDigits(): String = buildString(length) {
        this@toPersianDigits.forEach { character ->
            val index = latinDigits.indexOf(character)
            append(if (index >= 0) persianDigits[index] else character)
        }
    }

    @JvmStatic
    public fun String.toLatinDigits(): String = buildString(length) {
        this@toLatinDigits.forEach { character ->
            val persianIndex = persianDigits.indexOf(character)
            val arabicIndex = arabicIndicDigits.indexOf(character)
            append(
                when {
                    persianIndex >= 0 -> latinDigits.elementAt(persianIndex)
                    arabicIndex >= 0 -> latinDigits.elementAt(arabicIndex)
                    else -> character
                },
            )
        }
    }
}
