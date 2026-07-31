package com.msa.coreui.calendar.internal.ui

import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.internal.persianFormat
import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.calendar.model.PersianDateSelection

internal fun selectionSummary(
    selection: PersianDateSelection,
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean,
): String = when (selection) {
    is PersianDateSelection.Single -> selection.date
        ?.persianFormat(strings, usePersianDigits)
        ?: strings.noSelection

    is PersianDateSelection.Range -> {
        // Public properties declared in another module are not stable smart-cast targets.
        // Snapshot them once so nullability checks remain valid throughout formatting.
        val start = selection.start
        val end = selection.end
        when {
            start == null -> strings.noSelection
            end == null -> start.persianFormat(strings, usePersianDigits)
            else -> buildString {
                append(start.persianFormat(strings, usePersianDigits))
                append(' ')
                append(strings.rangeSeparator)
                append(' ')
                append(end.persianFormat(strings, usePersianDigits))
            }
        }
    }

    is PersianDateSelection.Multiple -> if (selection.dates.isEmpty()) {
        strings.noSelection
    } else {
        strings.selectedCount(selection.dates.size).withDigitStyle(usePersianDigits)
    }
}
