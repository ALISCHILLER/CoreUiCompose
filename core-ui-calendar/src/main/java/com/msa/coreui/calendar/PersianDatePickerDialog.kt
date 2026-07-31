package com.msa.coreui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.components.CoreButton
import com.msa.coreui.components.CoreButtonContent
import com.msa.coreui.components.CoreButtonSize
import com.msa.coreui.components.CoreButtonStyle
import com.msa.coreui.foundation.CoreUiTheme
import java.time.Clock
import java.time.LocalDate

/**
 * Adaptive dialog wrapper for [PersianDatePicker].
 *
 * The dialog keeps date selection in [state]. Confirmation does not dismiss the dialog automatically;
 * the caller owns that behavior so validation, analytics, and asynchronous persistence remain explicit.
 */
@Composable
fun PersianDatePickerDialog(
    state: PersianDatePickerState,
    onDismissRequest: () -> Unit,
    onConfirm: (PersianDateSelection) -> Unit,
    modifier: Modifier = Modifier,
    strings: PersianDatePickerStrings = defaultPersianDatePickerStrings(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    dimensions: PersianDatePickerDimensions = PersianDatePickerDimensions(),
    clock: Clock = Clock.systemDefaultZone(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    dateIndicatorCount: (LocalDate) -> Int = { 0 },
    dateAdditionalDescription: (LocalDate) -> String? = { null },
    onSelectionRejected: (PersianDateSelectionResult.Rejected) -> Unit = {},
) {
    val today = remember(clock) { LocalDate.now(clock) }
    val todayInsideBoundary = today in state.config.minDate..state.config.maxDate
    val spacing = CoreUiTheme.spacing
    val elevation = CoreUiTheme.elevation
    val sizing = CoreUiTheme.sizing

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.lg, vertical = spacing.xl),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = modifier
                    .widthIn(max = sizing.dialogMaxWidth)
                    .heightIn(max = sizing.dialogMaxHeight)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = elevation.level2,
                shadowElevation = elevation.level4,
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = spacing.xl,
                                end = spacing.lg,
                                top = spacing.lg,
                                bottom = spacing.sm,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        Text(
                            text = strings.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 2,
                        )
                        if (state.config.showTodayAction) {
                            CoreButton(
                                content = CoreButtonContent(strings.today),
                                onClick = { state.jumpToToday(clock) },
                                enabled = todayInsideBoundary,
                                style = CoreButtonStyle.TERTIARY,
                                size = CoreButtonSize.COMPACT,
                            )
                        }
                    }

                    val pickerScrollModifier = if (state.view == PersianDatePickerView.DAYS) {
                        Modifier.verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .then(pickerScrollModifier),
                    ) {
                        PersianDatePicker(
                            state = state,
                            modifier = Modifier.fillMaxWidth(),
                            strings = strings,
                            colors = colors,
                            dimensions = dimensions,
                            clock = clock,
                            dateIndicatorCount = dateIndicatorCount,
                            dateAdditionalDescription = dateAdditionalDescription,
                            onSelectionRejected = onSelectionRejected,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(top = spacing.sm))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.lg, vertical = spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CoreButton(
                            content = CoreButtonContent(strings.cancel),
                            onClick = onDismissRequest,
                            style = CoreButtonStyle.TERTIARY,
                        )
                        CoreButton(
                            content = CoreButtonContent(strings.confirm),
                            onClick = { onConfirm(state.selection) },
                            enabled = state.canConfirm,
                            style = CoreButtonStyle.PRIMARY,
                        )
                    }
                }
            }
        }
    }
}
