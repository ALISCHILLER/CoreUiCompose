package com.msa.coreui.calendar.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import com.msa.coreui.calendar.PersianDateAvailability
import com.msa.coreui.calendar.PersianDatePickerColors
import com.msa.coreui.calendar.PersianDatePickerDimensions
import com.msa.coreui.calendar.PersianDatePickerState
import com.msa.coreui.calendar.PersianDateSelectionResult
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.PersianDatePickerTestTags
import com.msa.coreui.calendar.internal.format
import com.msa.coreui.calendar.internal.indexFrom
import com.msa.coreui.calendar.internal.weekdayLabelOrder
import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.calendar.model.PersianDate
import com.msa.coreui.calendar.model.PersianDateSelectionMode
import com.msa.coreui.foundation.CoreUiTheme
import java.time.LocalDate
import kotlin.math.abs

@Composable
internal fun DaysView(
    state: PersianDatePickerState,
    strings: PersianDatePickerStrings,
    colors: PersianDatePickerColors,
    today: LocalDate,
    usePersianDigits: Boolean,
    dateIndicatorCount: (LocalDate) -> Int,
    dateAdditionalDescription: (LocalDate) -> String?,
    dimensions: PersianDatePickerDimensions,
    onSelectionRejected: (PersianDateSelectionResult.Rejected) -> Unit,
) {
    val dates = remember(state.displayedMonth, state.config.firstDayOfWeek) {
        val firstDate = state.displayedMonth.firstDay.toGregorian()
        val gridStart = firstDate.minusDays(
            firstDate.dayOfWeek.indexFrom(state.config.firstDayOfWeek).toLong(),
        )
        List(DAY_GRID_CELL_COUNT) { index -> gridStart.plusDays(index.toLong()) }
    }
    val weekdayOrder = remember(state.config.firstDayOfWeek) {
        weekdayLabelOrder(state.config.firstDayOfWeek)
    }
    val layoutDirection = LocalLayoutDirection.current
    val swipeThreshold = with(LocalDensity.current) { CoreUiTheme.sizing.touchTarget.toPx() }
    val swipeModifier = if (state.config.enableSwipeNavigation) {
        Modifier.pointerInput(state.displayedMonth, layoutDirection, swipeThreshold) {
            var distance = 0f
            detectHorizontalDragGestures(
                onDragStart = { distance = 0f },
                onHorizontalDrag = { change, dragAmount ->
                    distance += dragAmount
                    change.consume()
                },
                onDragCancel = { distance = 0f },
                onDragEnd = {
                    if (abs(distance) >= swipeThreshold) {
                        val ltrDelta = if (distance < 0f) 1 else -1
                        val delta = if (layoutDirection == LayoutDirection.Ltr) ltrDelta else -ltrDelta
                        state.tryNavigateMonths(delta)
                    }
                    distance = 0f
                },
            )
        }
    } else {
        Modifier
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().then(swipeModifier)) {
        val horizontalPadding = if (maxWidth >= dimensions.dayGridPaddingBreakpoint) CoreUiTheme.spacing.md else CoreUiTheme.spacing.none
        val selectionGroupModifier = if (state.selectionMode == PersianDateSelectionMode.SINGLE) {
            Modifier.selectableGroup()
        } else {
            Modifier
        }
        Column(
            modifier = Modifier
                .testTag(PersianDatePickerTestTags.DAY_GRID)
                .fillMaxWidth()
                .then(selectionGroupModifier)
                .semantics {
                    collectionInfo = CollectionInfo(
                        rowCount = DAY_GRID_ROW_COUNT,
                        columnCount = DAYS_PER_WEEK,
                    )
                }
                .padding(horizontal = horizontalPadding)
                .padding(bottom = CoreUiTheme.spacing.md),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayOrder.forEach { labelIndex ->
                    Text(
                        text = strings.shortWeekdayNames[labelIndex],
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = CoreUiTheme.spacing.sm)
                            .semantics {
                                contentDescription = strings.weekdayNames[labelIndex]
                            },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            dates.chunked(DAYS_PER_WEEK).forEachIndexed { rowIndex, week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEachIndexed { columnIndex, date ->
                        val persianDate = remember(date) {
                            PersianDate.tryFromGregorian(date)
                        }
                        if (persianDate == null) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(CoreUiTheme.sizing.touchTarget),
                            )
                        } else {
                            val belongsToDisplayedMonth =
                                persianDate.year == state.displayedMonth.year &&
                                    persianDate.month == state.displayedMonth.month

                            DayCell(
                                persianDate = persianDate,
                                belongsToDisplayedMonth = belongsToDisplayedMonth,
                                visible = belongsToDisplayedMonth || state.config.showAdjacentMonthDays,
                                availability = state.dateAvailability(date),
                                selected = state.isSelected(date),
                                selectionMode = state.selectionMode,
                                rowIndex = rowIndex,
                                columnIndex = columnIndex,
                                rangeStart = state.isRangeStart(date),
                                rangeEnd = state.isRangeEnd(date),
                                insideRange = state.isInsideRange(date),
                                today = date == today,
                                strings = strings,
                                colors = colors,
                                usePersianDigits = usePersianDigits,
                                indicatorCount = dateIndicatorCount(date).coerceAtLeast(0),
                                additionalDescription = dateAdditionalDescription(date),
                                dimensions = dimensions,
                                onClick = {
                                    when (val result = state.selectDateResult(date)) {
                                        is PersianDateSelectionResult.Rejected -> onSelectionRejected(result)
                                        is PersianDateSelectionResult.Changed -> {
                                            if (!belongsToDisplayedMonth && state.config.autoNavigateAdjacentMonth) {
                                                state.jumpTo(date)
                                            }
                                        }
                                        PersianDateSelectionResult.Unchanged -> Unit
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    persianDate: PersianDate,
    belongsToDisplayedMonth: Boolean,
    visible: Boolean,
    availability: PersianDateAvailability,
    selected: Boolean,
    selectionMode: PersianDateSelectionMode,
    rowIndex: Int,
    columnIndex: Int,
    rangeStart: Boolean,
    rangeEnd: Boolean,
    insideRange: Boolean,
    today: Boolean,
    strings: PersianDatePickerStrings,
    colors: PersianDatePickerColors,
    usePersianDigits: Boolean,
    indicatorCount: Int,
    additionalDescription: String?,
    dimensions: PersianDatePickerDimensions,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) {
        Spacer(modifier = modifier.height(CoreUiTheme.sizing.touchTarget))
        return
    }

    val enabled = availability == PersianDateAvailability.SELECTABLE
    val availabilityLabel = when (availability) {
        PersianDateAvailability.SELECTABLE -> null
        PersianDateAvailability.OUT_OF_BOUNDS -> strings.outOfBoundsLabel
        PersianDateAvailability.DISABLED_DATE -> strings.disabledDateLabel
        PersianDateAvailability.DISABLED_RANGE -> strings.disabledRangeLabel
        PersianDateAvailability.DISABLED_WEEKDAY -> strings.disabledWeekdayLabel
        PersianDateAvailability.MULTIPLE_SELECTION_LIMIT -> strings.selectionLimitReachedLabel
    }
    val description = listOfNotNull(
        persianDate.format(strings, usePersianDigits),
        strings.adjacentMonthLabel.takeUnless { belongsToDisplayedMonth },
        strings.indicatorCountLabel(indicatorCount)
            .withDigitStyle(usePersianDigits)
            .takeIf { indicatorCount > 0 },
        additionalDescription?.takeIf(String::isNotBlank),
    ).joinToString(separator = ", ")
    val foreground = when {
        selected -> colors.selectedContent
        !enabled -> colors.disabledContent
        !belongsToDisplayedMonth -> colors.adjacentMonthContent
        else -> colors.dayContent
    }
    val stateLabels = buildList {
        if (rangeStart) add(strings.rangeStartLabel)
        if (rangeEnd) add(strings.rangeEndLabel)
        if (selected && !rangeStart && !rangeEnd) add(strings.selectedLabel)
        if (insideRange) add(strings.inRangeLabel)
        availabilityLabel?.let(::add)
        if (today) add(strings.today)
    }
    val selectionRole = when (selectionMode) {
        PersianDateSelectionMode.SINGLE -> Role.RadioButton
        PersianDateSelectionMode.RANGE -> Role.Button
        PersianDateSelectionMode.MULTIPLE -> Role.Checkbox
    }
    val semanticsModifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = description
        role = selectionRole
        collectionItemInfo = CollectionItemInfo(
            rowIndex = rowIndex,
            rowSpan = 1,
            columnIndex = columnIndex,
            columnSpan = 1,
        )
        if (selectionMode == PersianDateSelectionMode.MULTIPLE) {
            toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
        } else {
            this.selected = selected
        }
        if (stateLabels.isNotEmpty()) stateDescription = stateLabels.joinToString(separator = ", ")
        if (!enabled) disabled()
    }

    Box(
        modifier = modifier
            .testTag(PersianDatePickerTestTags.day(persianDate.toGregorian()))
            .height(CoreUiTheme.sizing.touchTarget)
            .then(if (insideRange) Modifier.background(colors.rangeContainer) else Modifier)
            .then(semanticsModifier)
            .clickable(enabled = enabled, role = selectionRole, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.dayContentSize)
                .then(
                    when {
                        selected -> Modifier.background(colors.selectedContainer, CircleShape)
                        today -> Modifier.border(CoreUiTheme.stroke.emphasis, colors.todayBorder, CircleShape)
                        else -> Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = persianDate.day.toString().withDigitStyle(usePersianDigits),
                    color = foreground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected || today) FontWeight.SemiBold else FontWeight.Normal,
                )
                if (indicatorCount > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xxs)) {
                        repeat(indicatorCount.coerceAtMost(MAX_VISIBLE_INDICATORS)) {
                            Box(
                                modifier = Modifier
                                    .size(dimensions.indicatorSize)
                                    .background(
                                        color = if (selected) colors.selectedContent else colors.todayBorder,
                                        shape = CircleShape,
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val DAY_GRID_ROW_COUNT = 6
private const val DAYS_PER_WEEK = 7
private const val DAY_GRID_CELL_COUNT = DAY_GRID_ROW_COUNT * DAYS_PER_WEEK
private const val MAX_VISIBLE_INDICATORS = 3
