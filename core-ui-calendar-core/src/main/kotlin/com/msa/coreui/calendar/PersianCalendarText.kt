package com.msa.coreui.calendar

import java.util.Collections

/** Locale-neutral calendar labels that can be reused outside Compose. */
public object PersianCalendarText {
    @JvmField
    public val PERSIAN_MONTH_NAMES: List<String> = immutableListOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
    )

    @JvmField
    public val ENGLISH_MONTH_NAMES: List<String> = immutableListOf(
        "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand",
    )

    @JvmField
    public val PERSIAN_WEEKDAY_NAMES_SATURDAY_FIRST: List<String> = immutableListOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه",
    )

    @JvmField
    public val ENGLISH_WEEKDAY_NAMES_SATURDAY_FIRST: List<String> = immutableListOf(
        "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
    )

    private fun immutableListOf(vararg values: String): List<String> =
        Collections.unmodifiableList(values.toList())
}
