package com.msa.coreui.calendar.internal.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.msa.coreui.calendar.PersianDatePickerDimensions
import com.msa.coreui.calendar.PersianDatePickerState
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.PersianDatePickerTestTags
import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.foundation.CoreUiTheme

@Composable
internal fun MonthsView(
    state: PersianDatePickerState,
    strings: PersianDatePickerStrings,
    dimensions: PersianDatePickerDimensions,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensions.monthCellMinWidth),
        modifier = Modifier
            .testTag(PersianDatePickerTestTags.MONTH_GRID)
            .fillMaxWidth()
            .height(dimensions.monthSelectorHeight)
            .padding(horizontal = CoreUiTheme.spacing.lg, vertical = CoreUiTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
    ) {
        items(
            items = strings.monthNames.withIndex().toList(),
            key = { indexed -> indexed.index },
            contentType = { "month" },
        ) { indexed ->
            val month = indexed.index + 1
            val selected = month == state.displayedMonth.month
            val available = state.isMonthAvailable(month)
            val selectable = available && (!state.config.skipFullyDisabledMonths || state.isMonthSelectable(state.displayedMonth.year, month))
            OutlinedButton(
                onClick = { state.selectMonth(month) },
                enabled = selectable,
                modifier = Modifier
                    .testTag(PersianDatePickerTestTags.month(month))
                    .fillMaxWidth()
                    .sizeIn(minHeight = CoreUiTheme.sizing.touchTarget)
                    .semantics {
                        role = Role.Button
                        this.selected = selected
                        contentDescription = indexed.value
                        when {
                            selected -> stateDescription = strings.selectedLabel
                            !selectable -> {
                                disabled()
                                stateDescription = strings.monthUnavailableLabel
                            }
                        }
                    },
                border = BorderStroke(
                    width = if (selected) CoreUiTheme.stroke.strong else CoreUiTheme.stroke.hairline,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
                Text(
                    text = indexed.value,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
internal fun YearsView(
    state: PersianDatePickerState,
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean,
    dimensions: PersianDatePickerDimensions,
) {
    val years = remember(state.availableYears) { state.availableYears.toList() }
    val currentIndex = (state.displayedMonth.year - state.availableYears.first).coerceAtLeast(0)
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = (currentIndex - YEAR_SCROLL_CONTEXT).coerceAtLeast(0),
    )

    LaunchedEffect(state.availableYears, currentIndex) {
        gridState.scrollToItem((currentIndex - YEAR_SCROLL_CONTEXT).coerceAtLeast(0))
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensions.yearCellMinWidth),
        state = gridState,
        modifier = Modifier
            .testTag(PersianDatePickerTestTags.YEAR_GRID)
            .fillMaxWidth()
            .height(dimensions.yearSelectorHeight)
            .padding(horizontal = CoreUiTheme.spacing.lg, vertical = CoreUiTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
    ) {
        items(
            items = years,
            key = { year -> year },
            contentType = { "year" },
        ) { year ->
            val selected = year == state.displayedMonth.year
            val selectable = !state.config.skipFullyDisabledMonths || state.isYearSelectable(year)
            val displayedYear = year.toString().withDigitStyle(usePersianDigits)
            OutlinedButton(
                onClick = { state.selectYear(year) },
                enabled = selectable,
                modifier = Modifier
                    .testTag(PersianDatePickerTestTags.year(year))
                    .fillMaxWidth()
                    .sizeIn(minHeight = CoreUiTheme.sizing.touchTarget)
                    .semantics {
                        role = Role.Button
                        this.selected = selected
                        contentDescription = displayedYear
                        when {
                            selected -> stateDescription = strings.selectedLabel
                            !selectable -> {
                                disabled()
                                stateDescription = strings.yearUnavailableLabel
                            }
                        }
                    },
                border = BorderStroke(
                    width = if (selected) CoreUiTheme.stroke.strong else CoreUiTheme.stroke.hairline,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
                Text(
                    text = displayedYear,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

private const val YEAR_SCROLL_CONTEXT = 8
