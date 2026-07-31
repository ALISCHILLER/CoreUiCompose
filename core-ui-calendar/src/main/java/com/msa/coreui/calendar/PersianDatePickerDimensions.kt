package com.msa.coreui.calendar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Layout dimensions for [PersianDatePicker]. */
@Immutable
class PersianDatePickerDimensions(
    val dayContentSize: Dp = 40.dp,
    val indicatorSize: Dp = 4.dp,
    val monthCellMinWidth: Dp = 104.dp,
    val yearCellMinWidth: Dp = 76.dp,
    val monthSelectorHeight: Dp = 288.dp,
    val yearSelectorHeight: Dp = 304.dp,
    val dayGridPaddingBreakpoint: Dp = 360.dp,
) {
    init {
        require(
            listOf(
                dayContentSize,
                indicatorSize,
                monthCellMinWidth,
                yearCellMinWidth,
                monthSelectorHeight,
                yearSelectorHeight,
                dayGridPaddingBreakpoint,
            ).all { it > 0.dp },
        ) { "Persian date-picker dimensions must be greater than zero." }
        require(indicatorSize <= dayContentSize) {
            "indicatorSize must not exceed dayContentSize."
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is PersianDatePickerDimensions &&
            dayContentSize == other.dayContentSize &&
            indicatorSize == other.indicatorSize &&
            monthCellMinWidth == other.monthCellMinWidth &&
            yearCellMinWidth == other.yearCellMinWidth &&
            monthSelectorHeight == other.monthSelectorHeight &&
            yearSelectorHeight == other.yearSelectorHeight &&
            dayGridPaddingBreakpoint == other.dayGridPaddingBreakpoint

    override fun hashCode(): Int {
        var result = dayContentSize.hashCode()
        result = 31 * result + indicatorSize.hashCode()
        result = 31 * result + monthCellMinWidth.hashCode()
        result = 31 * result + yearCellMinWidth.hashCode()
        result = 31 * result + monthSelectorHeight.hashCode()
        result = 31 * result + yearSelectorHeight.hashCode()
        result = 31 * result + dayGridPaddingBreakpoint.hashCode()
        return result
    }

    override fun toString(): String =
        "PersianDatePickerDimensions(" +
            "dayContentSize=$dayContentSize, " +
            "indicatorSize=$indicatorSize, " +
            "monthCellMinWidth=$monthCellMinWidth, " +
            "yearCellMinWidth=$yearCellMinWidth, " +
            "monthSelectorHeight=$monthSelectorHeight, " +
            "yearSelectorHeight=$yearSelectorHeight, " +
            "dayGridPaddingBreakpoint=$dayGridPaddingBreakpoint)"
}
