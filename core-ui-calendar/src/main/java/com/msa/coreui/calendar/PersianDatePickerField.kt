package com.msa.coreui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.calendar.internal.usePersianDigits
import com.msa.coreui.calendar.internal.withDigitStyle
import com.msa.coreui.components.CoreFieldStatus
import com.msa.coreui.components.CoreIcon
import com.msa.coreui.components.CoreIcons
import com.msa.coreui.components.CoreTextField
import com.msa.coreui.foundation.CoreUiTheme
import java.time.Clock
import java.time.LocalDate

/**
 * Convenience field that owns dialog visibility while keeping the selected value controlled by the caller.
 * Selection values remain Gregorian [LocalDate] instances through [PersianDateSelection].
 */
@Composable
fun PersianDatePickerField(
    selection: PersianDateSelection,
    onSelectionChange: (PersianDateSelection) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    config: PersianDatePickerConfig = PersianDatePickerConfig(),
    strings: PersianDatePickerStrings = defaultPersianDatePickerStrings(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    dimensions: PersianDatePickerDimensions = PersianDatePickerDimensions(),
    clock: Clock = Clock.systemDefaultZone(),
    enabled: Boolean = true,
    required: Boolean = false,
    supportingText: String? = null,
    status: CoreFieldStatus = CoreFieldStatus.DEFAULT,
    statusDescription: String? = supportingText,
    placeholder: String = strings.noSelection,
    contentDescription: String = label,
    properties: androidx.compose.ui.window.DialogProperties = androidx.compose.ui.window.DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
    ),
    onDialogVisibilityChange: (Boolean) -> Unit = {},
    dateIndicatorCount: (LocalDate) -> Int = { 0 },
    dateAdditionalDescription: (LocalDate) -> String? = { null },
    onSelectionRejected: (PersianDateSelectionResult.Rejected) -> Unit = {},
) {
    require(label.isNotBlank()) { "label must not be blank." }
    require(placeholder.isNotBlank()) { "placeholder must not be blank." }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    require(supportingText == null || supportingText.isNotBlank()) {
        "supportingText must be null or non-blank."
    }
    require(statusDescription == null || statusDescription.isNotBlank()) {
        "statusDescription must be null or non-blank."
    }

    var dialogVisible by rememberSaveable { mutableStateOf(false) }
    fun updateDialogVisibility(visible: Boolean) {
        if (dialogVisible != visible) {
            dialogVisible = visible
            onDialogVisibilityChange(visible)
        }
    }
    val state = rememberPersianDatePickerState(
        selectionMode = selection.mode,
        initialSelection = selection,
        config = config,
        clock = clock,
    )
    LaunchedEffect(selection, dialogVisible) {
        if (state.selection != selection) {
            state.replaceSelection(selection)
        }
        if (dialogVisible) state.jumpToSelection()
    }
    LaunchedEffect(enabled) {
        if (!enabled && dialogVisible) updateDialogVisibility(false)
    }

    val usePersianDigits = config.usePersianDigits(strings)
    val displayValue = formatSelection(
        selection = selection,
        strings = strings,
        usePersianDigits = usePersianDigits,
    )
    val openActionLabel = CoreUiTheme.strings.open
    val fieldStateDescription = listOfNotNull(
        displayValue.ifBlank { placeholder },
        CoreUiTheme.strings.required.takeIf { required },
        statusDescription,
    ).joinToString(", ")
    Box(modifier = modifier) {
        CoreTextField(
            value = displayValue,
            onValueChange = {},
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics { },
            placeholder = placeholder,
            enabled = enabled,
            readOnly = true,
            required = required,
            supportingText = supportingText,
            status = status,
            statusDescription = statusDescription,
            trailingIcon = {
                CoreIcon(
                    imageVector = CoreIcons.Calendar,
                    contentDescription = null,
                    decorative = true,
                )
            },
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = {
                        state.clearSelectionFeedback()
                        state.jumpToSelection()
                        updateDialogVisibility(true)
                    },
                )
                .clearAndSetSemantics {
                    role = Role.Button
                    this.contentDescription = contentDescription
                    stateDescription = fieldStateDescription
                    if (enabled) {
                        onClick(label = openActionLabel, action = {
                            state.clearSelectionFeedback()
                            state.jumpToSelection()
                            updateDialogVisibility(true)
                            true
                        })
                    } else {
                        disabled()
                    }
                },
        )
    }

    if (dialogVisible) {
        PersianDatePickerDialog(
            state = state,
            onDismissRequest = { updateDialogVisibility(false) },
            onConfirm = { confirmed ->
                onSelectionChange(confirmed)
                updateDialogVisibility(false)
            },
            strings = strings,
            colors = colors,
            dimensions = dimensions,
            clock = clock,
            dateIndicatorCount = dateIndicatorCount,
            dateAdditionalDescription = dateAdditionalDescription,
            onSelectionRejected = onSelectionRejected,
            properties = properties,
        )
    }
}

internal fun formatSelection(
    selection: PersianDateSelection,
    strings: PersianDatePickerStrings,
    usePersianDigits: Boolean = strings.defaultToPersianDigits,
): String = PersianDateSelectionFormatter.format(
    selection = selection,
    strings = strings,
    usePersianDigits = usePersianDigits,
)
