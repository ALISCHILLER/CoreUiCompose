package com.msa.coreui.calendar.internal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import com.msa.coreui.calendar.PersianDateConfirmationStatus
import com.msa.coreui.calendar.PersianDatePickerColors
import com.msa.coreui.calendar.PersianDatePickerState
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.PersianDatePickerTestTags
import com.msa.coreui.calendar.PersianDateSelectionRejection
import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.calendar.model.PersianDateSelectionMode
import com.msa.coreui.components.CoreButton
import com.msa.coreui.components.CoreButtonContent
import com.msa.coreui.components.CoreButtonSize
import com.msa.coreui.components.CoreButtonStyle
import com.msa.coreui.components.CoreIcon
import com.msa.coreui.components.CoreIconButton
import com.msa.coreui.components.CoreIcons
import com.msa.coreui.foundation.CoreUiTheme

@Composable
internal fun PickerHero(
    state: PersianDatePickerState,
    strings: PersianDatePickerStrings,
    colors: PersianDatePickerColors,
    usePersianDigits: Boolean,
) {
    val hint = when (state.selectionMode) {
        PersianDateSelectionMode.SINGLE -> strings.singleSelectionHint
        PersianDateSelectionMode.RANGE -> strings.rangeSelectionHint
        PersianDateSelectionMode.MULTIPLE -> strings.multipleSelectionHint
    }
    val rejectionMessage = state.lastSelectionRejection?.toMessage(strings)
    val requirementMessage = state.confirmationRequirementMessage(strings, usePersianDigits)

    Surface(
        color = colors.headerContainer,
        contentColor = colors.headerContent,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = CoreUiTheme.spacing.xl, vertical = CoreUiTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = hint,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                )
                if (state.hasSelection) {
                    CoreButton(
                        content = CoreButtonContent(strings.clear),
                        onClick = state::clearSelection,
                        style = CoreButtonStyle.TERTIARY,
                        size = CoreButtonSize.COMPACT,
                    )
                }
            }
            Text(
                text = selectionSummary(state.selection, strings, usePersianDigits),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
            if (rejectionMessage != null) {
                Text(
                    text = rejectionMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Assertive
                        contentDescription = rejectionMessage
                    },
                )
            } else if (requirementMessage != null) {
                Text(
                    text = requirementMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
internal fun PickerNavigation(
    state: PersianDatePickerState,
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CoreUiTheme.spacing.md, vertical = CoreUiTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoreIconButton(
            onClick = { state.navigateMonths(-1) },
            contentDescription = strings.previousMonth,
            enabled = state.canNavigatePrevious,
            modifier = Modifier.testTag(PersianDatePickerTestTags.PREVIOUS_MONTH),
        ) {
            CoreIcon(
                imageVector = if (isRtl) CoreIcons.ChevronRight else CoreIcons.ChevronLeft,
                contentDescription = null,
                decorative = true,
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = state::showMonths,
                modifier = Modifier
                    .testTag(PersianDatePickerTestTags.MONTH_SELECTOR)
                    .semantics { contentDescription = strings.selectMonth },
            ) {
                Text(
                    text = strings.monthNames[state.displayedMonth.month - 1],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            TextButton(
                onClick = state::showYears,
                modifier = Modifier
                    .testTag(PersianDatePickerTestTags.YEAR_SELECTOR)
                    .semantics { contentDescription = strings.selectYear },
            ) {
                Text(
                    text = state.displayedMonth.year.toString().withDigitStyle(usePersianDigits),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        CoreIconButton(
            onClick = { state.navigateMonths(1) },
            contentDescription = strings.nextMonth,
            enabled = state.canNavigateNext,
            modifier = Modifier.testTag(PersianDatePickerTestTags.NEXT_MONTH),
        ) {
            CoreIcon(
                imageVector = if (isRtl) CoreIcons.ChevronLeft else CoreIcons.ChevronRight,
                contentDescription = null,
                decorative = true,
            )
        }
    }
}

private fun PersianDateSelectionRejection.toMessage(strings: PersianDatePickerStrings): String = when (this) {
    PersianDateSelectionRejection.OUT_OF_BOUNDS -> strings.outOfBoundsLabel
    PersianDateSelectionRejection.DISABLED_DATE -> strings.disabledDateLabel
    PersianDateSelectionRejection.DISABLED_RANGE -> strings.disabledRangeLabel
    PersianDateSelectionRejection.DISABLED_WEEKDAY -> strings.disabledWeekdayLabel
    PersianDateSelectionRejection.MULTIPLE_SELECTION_LIMIT -> strings.selectionLimitReachedLabel
    PersianDateSelectionRejection.RANGE_CONTAINS_DISABLED_DATE -> strings.rangeContainsDisabledDateLabel
    PersianDateSelectionRejection.RANGE_TOO_SHORT -> strings.rangeTooShortLabel
    PersianDateSelectionRejection.RANGE_TOO_LONG -> strings.rangeTooLongLabel
}

private fun PersianDatePickerState.confirmationRequirementMessage(
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean,
): String? = when (confirmationStatus) {
    PersianDateConfirmationStatus.NO_SELECTABLE_DATES -> strings.noSelectableDatesLabel
    PersianDateConfirmationStatus.MULTIPLE_SELECTION_BELOW_MINIMUM ->
        strings.minimumSelectionCountLabel(config.minMultipleSelection)
            .withDigitStyle(usePersianDigits)
    PersianDateConfirmationStatus.RANGE_END_REQUIRED -> config.minRangeDays
        .takeIf { it > 1 }
        ?.let(strings.minimumRangeDaysLabel)
        ?.withDigitStyle(usePersianDigits)
    PersianDateConfirmationStatus.READY,
    PersianDateConfirmationStatus.EMPTY_SELECTION_ALLOWED,
    PersianDateConfirmationStatus.EMPTY_SELECTION_REQUIRED,
    -> null
}
