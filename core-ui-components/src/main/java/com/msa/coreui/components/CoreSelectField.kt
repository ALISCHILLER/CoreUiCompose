package com.msa.coreui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import com.msa.coreui.foundation.CoreUiTheme

/**
 * Controlled, read-only select field backed by an accessible dropdown menu.
 *
 * The selected value remains owned by the caller. The menu width follows the field width and every
 * option exposes its enabled and selected states to accessibility services.
 */
@Composable
fun <T : Any> CoreSelectField(
    options: List<CoreSelectOption<T>>,
    selectedValue: T?,
    onValueSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    supportingText: String? = null,
    status: CoreFieldStatus = CoreFieldStatus.DEFAULT,
    statusDescription: String? = null,
    fieldContentDescription: String = label,
    selectedValueLabel: String? = null,
    unavailableSelectionText: String = CoreUiTheme.strings.selectionUnavailable,
) {
    require(options.map { option -> option.value }.distinct().size == options.size) {
        "Option values must be unique."
    }
    require(label.isNotBlank()) { "label must not be blank." }
    require(placeholder == null || placeholder.isNotBlank()) { "placeholder must be null or non-blank." }
    require(supportingText == null || supportingText.isNotBlank()) {
        "supportingText must be null or non-blank."
    }
    require(statusDescription == null || statusDescription.isNotBlank()) {
        "statusDescription must be null or non-blank."
    }
    require(fieldContentDescription.isNotBlank()) { "fieldContentDescription must not be blank." }
    require(selectedValueLabel == null || selectedValueLabel.isNotBlank()) {
        "selectedValueLabel must be null or non-blank."
    }
    require(unavailableSelectionText.isNotBlank()) { "unavailableSelectionText must not be blank." }

    val optionValues = options.map { option -> option.value }
    val selectedOption = selectedValue?.let { value ->
        options.firstOrNull { option -> option.value == value }
    }
    val hasUnavailableSelection = selectedValue != null && selectedOption == null
    val hasEnabledOptions = options.any { option -> option.enabled }
    val interactionEnabled = enabled && hasEnabledOptions
    val unavailableMessage = CoreUiTheme.strings.noOptions
        .takeIf { !hasEnabledOptions && selectedOption == null }
    var expanded by remember { mutableStateOf(false) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val motion = CoreUiTheme.motion
    val openActionLabel = CoreUiTheme.strings.open
    val chevronRotation = if (motion.animationsEnabled) {
        animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(motion.fast),
            label = "Select field chevron",
        ).value
    } else if (expanded) {
        180f
    } else {
        0f
    }
    LaunchedEffect(interactionEnabled) {
        if (!interactionEnabled) expanded = false
    }
    LaunchedEffect(optionValues, selectedValue) {
        if (selectedValue != null && selectedValue !in optionValues) {
            expanded = false
        }
    }
    val displayedValue = selectedOption?.label
        ?: selectedValueLabel
        ?: ""
    val effectiveSupportingText = when {
        hasUnavailableSelection -> unavailableSelectionText
        supportingText != null -> supportingText
        else -> unavailableMessage
    }
    val effectiveStatus = if (hasUnavailableSelection) CoreFieldStatus.ERROR else status
    val stateLabel = buildList {
        add(
            selectedOption?.contentDescription
                ?: unavailableSelectionText.takeIf { hasUnavailableSelection }
                ?: unavailableMessage
                ?: placeholder
                ?: CoreUiTheme.strings.notSelected,
        )
        add(if (expanded) CoreUiTheme.strings.expanded else CoreUiTheme.strings.collapsed)
        if (required) add(CoreUiTheme.strings.required)
        statusDescription?.takeUnless { hasUnavailableSelection }?.let(::add)
    }.filter(String::isNotBlank).joinToString(", ")

    Box(
        modifier = modifier.onSizeChanged { measured -> fieldSize = measured },
    ) {
        CoreTextField(
            value = displayedValue,
            onValueChange = {},
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics { },
            placeholder = placeholder,
            enabled = interactionEnabled,
            readOnly = true,
            required = required,
            supportingText = effectiveSupportingText,
            status = effectiveStatus,
            statusDescription = if (hasUnavailableSelection) unavailableSelectionText else statusDescription,
            trailingIcon = {
                CoreIcon(
                    imageVector = CoreIcons.ChevronDown,
                    contentDescription = null,
                    decorative = true,
                    modifier = Modifier.rotate(chevronRotation),
                )
            },
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    enabled = interactionEnabled,
                    role = Role.Button,
                    onClick = { expanded = !expanded },
                )
                .clearAndSetSemantics {
                    role = Role.Button
                    contentDescription = fieldContentDescription
                    stateDescription = stateLabel
                    if (interactionEnabled) {
                        onClick(
                            label = openActionLabel,
                            action = {
                                expanded = !expanded
                                true
                            },
                        )
                    } else {
                        disabled()
                    }
                },
        )

        val menuWidthModifier = if (fieldSize.width > 0) {
            Modifier.width(with(density) { fieldSize.width.toDp() })
        } else {
            Modifier.fillMaxWidth()
        }
        DropdownMenu(
            expanded = expanded && interactionEnabled,
            onDismissRequest = { expanded = false },
            modifier = menuWidthModifier.heightIn(max = CoreUiTheme.sizing.selectMenuMaxHeight),
        ) {
            options.forEach { option ->
                val isSelected = option.value == selectedValue
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        if (option.enabled) {
                            onValueSelected(option.value)
                            expanded = false
                        }
                    },
                    enabled = option.enabled,
                    modifier = Modifier.clearAndSetSemantics {
                        role = Role.RadioButton
                        contentDescription = option.contentDescription
                        selected = isSelected
                        if (!option.enabled) disabled()
                        if (option.enabled) {
                            onClick(action = {
                                onValueSelected(option.value)
                                expanded = false
                                true
                            })
                        }
                    },
                    leadingIcon = if (isSelected) {
                        {
                            CoreIcon(
                                imageVector = CoreIcons.Check,
                                contentDescription = null,
                                decorative = true,
                                modifier = Modifier.size(CoreUiTheme.sizing.iconSmall),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
    }
}
