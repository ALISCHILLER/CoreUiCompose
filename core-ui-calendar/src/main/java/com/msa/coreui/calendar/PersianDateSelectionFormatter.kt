package com.msa.coreui.calendar

import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.calendar.model.PersianDateSelection

/**
 * Public formatter for date-picker selections.
 *
 * Unlike [PersianDateSelection.toString], this API produces localized, user-facing text and keeps
 * Gregorian storage details out of the presentation layer.
 */
object PersianDateSelectionFormatter {
    @JvmStatic
    @JvmOverloads
    fun format(
        selection: PersianDateSelection,
        strings: PersianDatePickerStrings = PersianDatePickerStrings.Persian,
        usePersianDigits: Boolean = strings.defaultToPersianDigits,
        emptyText: String = "",
    ): String {
        require(emptyText.isEmpty() || emptyText.isNotBlank()) { "emptyText must be empty or non-blank." }
        return when (selection) {
            is PersianDateSelection.Single -> selection.date?.let { date ->
                PersianDateFormatter.formatGregorian(
                    date = date,
                    monthNames = strings.monthNames,
                    usePersianDigits = usePersianDigits,
                )
            } ?: emptyText

            is PersianDateSelection.Range -> {
                val start = selection.start
                val end = selection.end
                when {
                    start == null -> emptyText
                    end == null -> PersianDateFormatter.formatGregorian(
                        date = start,
                        monthNames = strings.monthNames,
                        usePersianDigits = usePersianDigits,
                    )
                    else -> listOf(start, end).joinToString(" ${strings.rangeSeparator} ") { date ->
                        PersianDateFormatter.formatGregorian(
                            date = date,
                            monthNames = strings.monthNames,
                            usePersianDigits = usePersianDigits,
                        )
                    }
                }
            }

            is PersianDateSelection.Multiple -> if (selection.dates.isEmpty()) {
                emptyText
            } else {
                strings.selectedCount(selection.dates.size).withDigitStyle(usePersianDigits)
            }
        }
    }
}
