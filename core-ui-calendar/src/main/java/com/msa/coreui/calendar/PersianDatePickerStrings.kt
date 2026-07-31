package com.msa.coreui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.util.ArrayList
import java.util.Collections

/** All user-visible labels used by the picker. Custom lists are defensively copied. */
@Immutable
class PersianDatePickerStrings(
    val title: String,
    val cancel: String,
    val confirm: String,
    val today: String,
    val clear: String,
    val previousMonth: String,
    val nextMonth: String,
    val selectMonth: String,
    val selectYear: String,
    val singleSelectionHint: String,
    val rangeSelectionHint: String,
    val multipleSelectionHint: String,
    val noSelection: String,
    val selectedLabel: String,
    val rangeStartLabel: String,
    val rangeEndLabel: String,
    val inRangeLabel: String,
    val unavailableLabel: String,
    val outOfBoundsLabel: String = unavailableLabel,
    val disabledDateLabel: String = unavailableLabel,
    val disabledRangeLabel: String = unavailableLabel,
    val disabledWeekdayLabel: String = unavailableLabel,
    val selectionLimitReachedLabel: String = unavailableLabel,
    val rangeContainsDisabledDateLabel: String = unavailableLabel,
    val rangeTooShortLabel: String = unavailableLabel,
    val rangeTooLongLabel: String = unavailableLabel,
    val monthUnavailableLabel: String = unavailableLabel,
    val yearUnavailableLabel: String = unavailableLabel,
    val noSelectableDatesLabel: String = unavailableLabel,
    val selectedCount: (Int) -> String,
    val minimumSelectionCountLabel: (Int) -> String = { count -> "Select at least $count dates" },
    val minimumRangeDaysLabel: (Int) -> String = { count -> "Select a range of at least $count days" },
    val rangeSeparator: String,
    val defaultToPersianDigits: Boolean,
    monthNames: List<String>,
    shortWeekdayNames: List<String>,
    weekdayNames: List<String> = shortWeekdayNames,
    val adjacentMonthLabel: String = "Adjacent month",
    val indicatorCountLabel: (Int) -> String = { count -> "$count indicators" },
) {
    val monthNames: List<String> = immutableCopy(monthNames)
    val shortWeekdayNames: List<String> = immutableCopy(shortWeekdayNames)
    val weekdayNames: List<String> = immutableCopy(weekdayNames)

    init {
        val requiredLabels = listOf(
            title,
            cancel,
            confirm,
            today,
            clear,
            previousMonth,
            nextMonth,
            selectMonth,
            selectYear,
            singleSelectionHint,
            rangeSelectionHint,
            multipleSelectionHint,
            noSelection,
            selectedLabel,
            rangeStartLabel,
            rangeEndLabel,
            inRangeLabel,
            unavailableLabel,
            outOfBoundsLabel,
            disabledDateLabel,
            disabledRangeLabel,
            disabledWeekdayLabel,
            selectionLimitReachedLabel,
            rangeContainsDisabledDateLabel,
            rangeTooShortLabel,
            rangeTooLongLabel,
            monthUnavailableLabel,
            yearUnavailableLabel,
            noSelectableDatesLabel,
            rangeSeparator,
            adjacentMonthLabel,
        )
        require(requiredLabels.all(String::isNotBlank)) {
            "Persian date-picker labels must not be blank."
        }
        require(this.monthNames.size == 12) { "monthNames must contain 12 entries." }
        require(this.shortWeekdayNames.size == 7) { "shortWeekdayNames must contain 7 entries." }
        require(this.weekdayNames.size == 7) { "weekdayNames must contain 7 entries." }
        require(this.monthNames.all(String::isNotBlank)) { "monthNames must not contain blank entries." }
        require(this.shortWeekdayNames.all(String::isNotBlank)) { "shortWeekdayNames must not contain blank entries." }
        require(this.weekdayNames.all(String::isNotBlank)) { "weekdayNames must not contain blank entries." }
        require(selectedCount(0).isNotBlank() && selectedCount(1).isNotBlank()) {
            "selectedCount must return a non-blank accessibility label."
        }
        require(indicatorCountLabel(0).isNotBlank() && indicatorCountLabel(1).isNotBlank()) {
            "indicatorCountLabel must return a non-blank accessibility label."
        }
        require(minimumSelectionCountLabel(1).isNotBlank()) {
            "minimumSelectionCountLabel must return a non-blank label."
        }
        require(minimumRangeDaysLabel(1).isNotBlank()) {
            "minimumRangeDaysLabel must return a non-blank label."
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is PersianDatePickerStrings &&
            title == other.title &&
            cancel == other.cancel &&
            confirm == other.confirm &&
            today == other.today &&
            clear == other.clear &&
            previousMonth == other.previousMonth &&
            nextMonth == other.nextMonth &&
            selectMonth == other.selectMonth &&
            selectYear == other.selectYear &&
            singleSelectionHint == other.singleSelectionHint &&
            rangeSelectionHint == other.rangeSelectionHint &&
            multipleSelectionHint == other.multipleSelectionHint &&
            noSelection == other.noSelection &&
            selectedLabel == other.selectedLabel &&
            rangeStartLabel == other.rangeStartLabel &&
            rangeEndLabel == other.rangeEndLabel &&
            inRangeLabel == other.inRangeLabel &&
            unavailableLabel == other.unavailableLabel &&
            outOfBoundsLabel == other.outOfBoundsLabel &&
            disabledDateLabel == other.disabledDateLabel &&
            disabledRangeLabel == other.disabledRangeLabel &&
            disabledWeekdayLabel == other.disabledWeekdayLabel &&
            selectionLimitReachedLabel == other.selectionLimitReachedLabel &&
            rangeContainsDisabledDateLabel == other.rangeContainsDisabledDateLabel &&
            rangeTooShortLabel == other.rangeTooShortLabel &&
            rangeTooLongLabel == other.rangeTooLongLabel &&
            monthUnavailableLabel == other.monthUnavailableLabel &&
            yearUnavailableLabel == other.yearUnavailableLabel &&
            noSelectableDatesLabel == other.noSelectableDatesLabel &&
            selectedCount == other.selectedCount &&
            minimumSelectionCountLabel == other.minimumSelectionCountLabel &&
            minimumRangeDaysLabel == other.minimumRangeDaysLabel &&
            rangeSeparator == other.rangeSeparator &&
            defaultToPersianDigits == other.defaultToPersianDigits &&
            monthNames == other.monthNames &&
            shortWeekdayNames == other.shortWeekdayNames &&
            weekdayNames == other.weekdayNames &&
            adjacentMonthLabel == other.adjacentMonthLabel &&
            indicatorCountLabel == other.indicatorCountLabel

    /** Returns a validated copy while preserving defensive collection handling. */
    fun copy(
        title: String = this.title,
        cancel: String = this.cancel,
        confirm: String = this.confirm,
        today: String = this.today,
        clear: String = this.clear,
        previousMonth: String = this.previousMonth,
        nextMonth: String = this.nextMonth,
        selectMonth: String = this.selectMonth,
        selectYear: String = this.selectYear,
        singleSelectionHint: String = this.singleSelectionHint,
        rangeSelectionHint: String = this.rangeSelectionHint,
        multipleSelectionHint: String = this.multipleSelectionHint,
        noSelection: String = this.noSelection,
        selectedLabel: String = this.selectedLabel,
        rangeStartLabel: String = this.rangeStartLabel,
        rangeEndLabel: String = this.rangeEndLabel,
        inRangeLabel: String = this.inRangeLabel,
        unavailableLabel: String = this.unavailableLabel,
        outOfBoundsLabel: String = this.outOfBoundsLabel,
        disabledDateLabel: String = this.disabledDateLabel,
        disabledRangeLabel: String = this.disabledRangeLabel,
        disabledWeekdayLabel: String = this.disabledWeekdayLabel,
        selectionLimitReachedLabel: String = this.selectionLimitReachedLabel,
        rangeContainsDisabledDateLabel: String = this.rangeContainsDisabledDateLabel,
        rangeTooShortLabel: String = this.rangeTooShortLabel,
        rangeTooLongLabel: String = this.rangeTooLongLabel,
        monthUnavailableLabel: String = this.monthUnavailableLabel,
        yearUnavailableLabel: String = this.yearUnavailableLabel,
        noSelectableDatesLabel: String = this.noSelectableDatesLabel,
        selectedCount: (Int) -> String = this.selectedCount,
        minimumSelectionCountLabel: (Int) -> String = this.minimumSelectionCountLabel,
        minimumRangeDaysLabel: (Int) -> String = this.minimumRangeDaysLabel,
        rangeSeparator: String = this.rangeSeparator,
        defaultToPersianDigits: Boolean = this.defaultToPersianDigits,
        monthNames: List<String> = this.monthNames,
        shortWeekdayNames: List<String> = this.shortWeekdayNames,
        weekdayNames: List<String> = this.weekdayNames,
        adjacentMonthLabel: String = this.adjacentMonthLabel,
        indicatorCountLabel: (Int) -> String = this.indicatorCountLabel,
    ): PersianDatePickerStrings = PersianDatePickerStrings(
        title = title,
        cancel = cancel,
        confirm = confirm,
        today = today,
        clear = clear,
        previousMonth = previousMonth,
        nextMonth = nextMonth,
        selectMonth = selectMonth,
        selectYear = selectYear,
        singleSelectionHint = singleSelectionHint,
        rangeSelectionHint = rangeSelectionHint,
        multipleSelectionHint = multipleSelectionHint,
        noSelection = noSelection,
        selectedLabel = selectedLabel,
        rangeStartLabel = rangeStartLabel,
        rangeEndLabel = rangeEndLabel,
        inRangeLabel = inRangeLabel,
        unavailableLabel = unavailableLabel,
        outOfBoundsLabel = outOfBoundsLabel,
        disabledDateLabel = disabledDateLabel,
        disabledRangeLabel = disabledRangeLabel,
        disabledWeekdayLabel = disabledWeekdayLabel,
        selectionLimitReachedLabel = selectionLimitReachedLabel,
        rangeContainsDisabledDateLabel = rangeContainsDisabledDateLabel,
        rangeTooShortLabel = rangeTooShortLabel,
        rangeTooLongLabel = rangeTooLongLabel,
        monthUnavailableLabel = monthUnavailableLabel,
        yearUnavailableLabel = yearUnavailableLabel,
        noSelectableDatesLabel = noSelectableDatesLabel,
        selectedCount = selectedCount,
        minimumSelectionCountLabel = minimumSelectionCountLabel,
        minimumRangeDaysLabel = minimumRangeDaysLabel,
        rangeSeparator = rangeSeparator,
        defaultToPersianDigits = defaultToPersianDigits,
        monthNames = monthNames,
        shortWeekdayNames = shortWeekdayNames,
        weekdayNames = weekdayNames,
        adjacentMonthLabel = adjacentMonthLabel,
        indicatorCountLabel = indicatorCountLabel,
    )

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + cancel.hashCode()
        result = 31 * result + confirm.hashCode()
        result = 31 * result + today.hashCode()
        result = 31 * result + clear.hashCode()
        result = 31 * result + previousMonth.hashCode()
        result = 31 * result + nextMonth.hashCode()
        result = 31 * result + selectMonth.hashCode()
        result = 31 * result + selectYear.hashCode()
        result = 31 * result + singleSelectionHint.hashCode()
        result = 31 * result + rangeSelectionHint.hashCode()
        result = 31 * result + multipleSelectionHint.hashCode()
        result = 31 * result + noSelection.hashCode()
        result = 31 * result + selectedLabel.hashCode()
        result = 31 * result + rangeStartLabel.hashCode()
        result = 31 * result + rangeEndLabel.hashCode()
        result = 31 * result + inRangeLabel.hashCode()
        result = 31 * result + unavailableLabel.hashCode()
        result = 31 * result + outOfBoundsLabel.hashCode()
        result = 31 * result + disabledDateLabel.hashCode()
        result = 31 * result + disabledRangeLabel.hashCode()
        result = 31 * result + disabledWeekdayLabel.hashCode()
        result = 31 * result + selectionLimitReachedLabel.hashCode()
        result = 31 * result + rangeContainsDisabledDateLabel.hashCode()
        result = 31 * result + rangeTooShortLabel.hashCode()
        result = 31 * result + rangeTooLongLabel.hashCode()
        result = 31 * result + monthUnavailableLabel.hashCode()
        result = 31 * result + yearUnavailableLabel.hashCode()
        result = 31 * result + noSelectableDatesLabel.hashCode()
        result = 31 * result + selectedCount.hashCode()
        result = 31 * result + minimumSelectionCountLabel.hashCode()
        result = 31 * result + minimumRangeDaysLabel.hashCode()
        result = 31 * result + rangeSeparator.hashCode()
        result = 31 * result + defaultToPersianDigits.hashCode()
        result = 31 * result + monthNames.hashCode()
        result = 31 * result + shortWeekdayNames.hashCode()
        result = 31 * result + weekdayNames.hashCode()
        result = 31 * result + adjacentMonthLabel.hashCode()
        result = 31 * result + indicatorCountLabel.hashCode()
        return result
    }

    override fun toString(): String =
        "PersianDatePickerStrings(title=$title, defaultToPersianDigits=$defaultToPersianDigits)"

    companion object {
        val Persian: PersianDatePickerStrings = PersianDatePickerStrings(
            title = "انتخاب تاریخ",
            cancel = "انصراف",
            confirm = "تأیید",
            today = "امروز",
            clear = "پاک کردن",
            previousMonth = "ماه قبل",
            nextMonth = "ماه بعد",
            selectMonth = "انتخاب ماه",
            selectYear = "انتخاب سال",
            singleSelectionHint = "یک تاریخ را انتخاب کنید",
            rangeSelectionHint = "تاریخ شروع و پایان را انتخاب کنید",
            multipleSelectionHint = "چند تاریخ را انتخاب کنید",
            noSelection = "هنوز تاریخی انتخاب نشده است",
            selectedLabel = "انتخاب شده",
            rangeStartLabel = "شروع بازه",
            rangeEndLabel = "پایان بازه",
            inRangeLabel = "در بازه انتخابی",
            unavailableLabel = "غیرفعال",
            outOfBoundsLabel = "خارج از بازه مجاز",
            disabledDateLabel = "تاریخ غیرفعال",
            disabledRangeLabel = "در بازه غیرفعال",
            disabledWeekdayLabel = "این روز هفته غیرفعال است",
            selectionLimitReachedLabel = "حداکثر تعداد انتخاب تکمیل شده است",
            rangeContainsDisabledDateLabel = "بازه انتخابی شامل تاریخ غیرفعال است",
            rangeTooShortLabel = "بازه انتخابی کوتاه‌تر از حداقل مجاز است",
            rangeTooLongLabel = "بازه انتخابی بلندتر از حداکثر مجاز است",
            monthUnavailableLabel = "این ماه تاریخ قابل انتخاب ندارد",
            yearUnavailableLabel = "این سال تاریخ قابل انتخاب ندارد",
            noSelectableDatesLabel = "هیچ تاریخ قابل انتخابی در بازه تنظیم‌شده وجود ندارد",
            selectedCount = { count -> "$count تاریخ انتخاب شده" },
            minimumSelectionCountLabel = { count -> "حداقل $count تاریخ انتخاب کنید" },
            minimumRangeDaysLabel = { count -> "بازه‌ای حداقل $count روزه انتخاب کنید" },
            rangeSeparator = "تا",
            defaultToPersianDigits = true,
            monthNames = PersianCalendarText.PERSIAN_MONTH_NAMES,
            shortWeekdayNames = listOf("ش", "ی", "د", "س", "چ", "پ", "ج"),
            weekdayNames = PersianCalendarText.PERSIAN_WEEKDAY_NAMES_SATURDAY_FIRST,
            adjacentMonthLabel = "ماه مجاور",
            indicatorCountLabel = { count -> "$count نشانگر" },
        )

        val English: PersianDatePickerStrings = PersianDatePickerStrings(
            title = "Select date",
            cancel = "Cancel",
            confirm = "Confirm",
            today = "Today",
            clear = "Clear",
            previousMonth = "Previous month",
            nextMonth = "Next month",
            selectMonth = "Select month",
            selectYear = "Select year",
            singleSelectionHint = "Choose one date",
            rangeSelectionHint = "Choose a start and end date",
            multipleSelectionHint = "Choose multiple dates",
            noSelection = "No date selected",
            selectedLabel = "Selected",
            rangeStartLabel = "Range start",
            rangeEndLabel = "Range end",
            inRangeLabel = "In selected range",
            unavailableLabel = "Unavailable",
            outOfBoundsLabel = "Outside allowed range",
            disabledDateLabel = "Disabled date",
            disabledRangeLabel = "Inside disabled range",
            disabledWeekdayLabel = "This weekday is disabled",
            selectionLimitReachedLabel = "Selection limit reached",
            rangeContainsDisabledDateLabel = "The selected range contains an unavailable date",
            rangeTooShortLabel = "The selected range is shorter than the allowed minimum",
            rangeTooLongLabel = "The selected range is longer than the allowed maximum",
            monthUnavailableLabel = "This month has no selectable dates",
            yearUnavailableLabel = "This year has no selectable dates",
            noSelectableDatesLabel = "No selectable dates are available in the configured range",
            selectedCount = { count ->
                if (count == 1) "1 date selected" else "$count dates selected"
            },
            minimumSelectionCountLabel = { count -> "Select at least $count dates" },
            minimumRangeDaysLabel = { count -> "Select a range of at least $count days" },
            rangeSeparator = "to",
            defaultToPersianDigits = false,
            monthNames = PersianCalendarText.ENGLISH_MONTH_NAMES,
            shortWeekdayNames = listOf("Sa", "Su", "Mo", "Tu", "We", "Th", "Fr"),
            weekdayNames = PersianCalendarText.ENGLISH_WEEKDAY_NAMES_SATURDAY_FIRST,
            adjacentMonthLabel = "Adjacent month",
            indicatorCountLabel = { count ->
                if (count == 1) "1 indicator" else "$count indicators"
            },
        )

        private fun immutableCopy(values: List<String>): List<String> =
            Collections.unmodifiableList(ArrayList(values))
    }
}

/** Uses Persian labels for Persian locales and English labels otherwise. */
@Composable
fun defaultPersianDatePickerStrings(): PersianDatePickerStrings {
    val configuration = LocalConfiguration.current
    val language = configuration.locales[0]?.language.orEmpty()
    return remember(language) {
        if (language == "fa") PersianDatePickerStrings.Persian else PersianDatePickerStrings.English
    }
}
