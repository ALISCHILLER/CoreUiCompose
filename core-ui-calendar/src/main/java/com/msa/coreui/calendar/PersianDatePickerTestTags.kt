package com.msa.coreui.calendar

import java.time.LocalDate

/** Stable test tags for consumer-owned Compose UI and screenshot tests. */
object PersianDatePickerTestTags {
    const val ROOT: String = "core-ui:persian-date-picker"
    const val DAY_GRID: String = "core-ui:persian-date-picker:days"
    const val MONTH_GRID: String = "core-ui:persian-date-picker:months"
    const val YEAR_GRID: String = "core-ui:persian-date-picker:years"
    const val PREVIOUS_MONTH: String = "core-ui:persian-date-picker:previous-month"
    const val NEXT_MONTH: String = "core-ui:persian-date-picker:next-month"
    const val MONTH_SELECTOR: String = "core-ui:persian-date-picker:month-selector"
    const val YEAR_SELECTOR: String = "core-ui:persian-date-picker:year-selector"

    fun day(date: LocalDate): String = "core-ui:persian-date-picker:day:${date.toEpochDay()}"
    fun month(month: Int): String = "core-ui:persian-date-picker:month:$month"
    fun year(year: Int): String = "core-ui:persian-date-picker:year:$year"
}
