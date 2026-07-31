package com.msa.coreui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import com.msa.coreui.foundation.CoreUiTheme

/**
 * Controlled multi-select field that reuses [CoreSelectOption] and exposes every option as a
 * checkbox to accessibility services.
 */
@Composable
fun <T : Any> CoreMultiSelectField(
    options: List<CoreSelectOption<T>>,
    selectedValues: Set<T>,
    onSelectionChange: (Set<T>) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    maxSelection: Int? = null,
    supportingText: String? = null,
    status: CoreFieldStatus = CoreFieldStatus.DEFAULT,
    statusDescription: String? = null,
    fieldContentDescription: String = label,
    selectedSummary: ((List<CoreSelectOption<T>>) -> String)? = null,
    unavailableSelectionText: String = CoreUiTheme.strings.selectionUnavailable,
) {
    validateMultiSelectContract(options, maxSelection)
    require(label.isNotBlank()) { "label must not be blank." }
    require(placeholder == null || placeholder.isNotBlank()) { "placeholder must be null or non-blank." }
    require(supportingText == null || supportingText.isNotBlank()) {
        "supportingText must be null or non-blank."
    }
    require(statusDescription == null || statusDescription.isNotBlank()) {
        "statusDescription must be null or non-blank."
    }
    require(fieldContentDescription.isNotBlank()) { "fieldContentDescription must not be blank." }
    require(unavailableSelectionText.isNotBlank()) { "unavailableSelectionText must not be blank." }

    // Derive from content on every recomposition so callers updating a mutable backing list cannot
    // leave the field with stale option metadata. Public APIs still recommend immutable lists.
    val availableValues = options.mapTo(linkedSetOf()) { option -> option.value }
    val selectedOptions = options.filter { option -> option.value in selectedValues }
    val unavailableCount = selectedValues.size - selectedOptions.size
    val hasUnavailableSelection = unavailableCount > 0
    val hasOverLimitSelection = maxSelection?.let { selectedOptions.size > it } == true
    val hasEnabledOptions = options.any { option -> option.enabled }
    val interactionEnabled = enabled && hasEnabledOptions
    val atLimit = maxSelection?.let { selectedOptions.size >= it } == true

    var expanded by remember { mutableStateOf(false) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val motion = CoreUiTheme.motion
    val openActionLabel = CoreUiTheme.strings.open
    val chevronRotation = if (motion.animationsEnabled) {
        animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(motion.fast),
            label = "Multi-select field chevron",
        ).value
    } else if (expanded) {
        180f
    } else {
        0f
    }

    LaunchedEffect(interactionEnabled) {
        if (!interactionEnabled) expanded = false
    }
    LaunchedEffect(availableValues, selectedValues) {
        // Keep the menu stable while ordinary values are toggled, but close it when an async data
        // refresh invalidates any externally controlled selection.
        if (selectedValues.any { value -> value !in availableValues }) {
            expanded = false
        }
    }

    val displayedValue = when {
        selectedOptions.isEmpty() -> ""
        selectedSummary != null -> selectedSummary(selectedOptions)
        selectedOptions.size <= DEFAULT_INLINE_LABEL_LIMIT -> selectedOptions.joinToString { it.label }
        else -> CoreUiTheme.strings.itemsSelected(selectedOptions.size)
    }.also { summary ->
        require(summary.isNotBlank() || selectedOptions.isEmpty()) {
            "selectedSummary must return non-blank text when values are selected."
        }
    }
    val limitMessage = maxSelection
        ?.takeIf { atLimit }
        ?.let(CoreUiTheme.strings.selectionLimitReached)
    val effectiveSupportingText = when {
        hasUnavailableSelection -> unavailableSelectionText
        hasOverLimitSelection -> limitMessage
        supportingText != null -> supportingText
        !hasEnabledOptions -> CoreUiTheme.strings.noOptions
        else -> limitMessage
    }
    val effectiveStatus = if (hasUnavailableSelection || hasOverLimitSelection) {
        CoreFieldStatus.ERROR
    } else {
        status
    }
    val stateLabel = buildList {
        add(
            when {
                hasUnavailableSelection -> unavailableSelectionText
                hasOverLimitSelection -> limitMessage ?: CoreUiTheme.strings.error
                selectedOptions.isEmpty() -> placeholder ?: CoreUiTheme.strings.notSelected
                else -> CoreUiTheme.strings.itemsSelected(selectedOptions.size)
            },
        )
        add(if (expanded) CoreUiTheme.strings.expanded else CoreUiTheme.strings.collapsed)
        if (required) add(CoreUiTheme.strings.required)
        statusDescription
            ?.takeUnless { hasUnavailableSelection || hasOverLimitSelection }
            ?.let(::add)
        limitMessage
            ?.takeUnless { hasOverLimitSelection }
            ?.let(::add)
    }.filter(String::isNotBlank).joinToString(", ")

    Box(modifier = modifier.onSizeChanged { measured -> fieldSize = measured }) {
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
            statusDescription = when {
                hasUnavailableSelection -> unavailableSelectionText
                hasOverLimitSelection -> limitMessage
                else -> statusDescription
            },
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
                val selected = option.value in selectedValues
                val optionEnabled = option.enabled && (selected || !atLimit)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        if (optionEnabled) {
                            val sanitizedSelection = sanitizeMultiSelection(selectedValues, availableValues)
                            onSelectionChange(toggleMultiSelection(sanitizedSelection, option.value))
                        }
                    },
                    enabled = optionEnabled,
                    modifier = Modifier.clearAndSetSemantics {
                        role = Role.Checkbox
                        contentDescription = option.contentDescription
                        toggleableState = if (selected) ToggleableState.On else ToggleableState.Off
                        if (!optionEnabled) disabled()
                        if (optionEnabled) {
                            onClick(action = {
                                val sanitizedSelection = sanitizeMultiSelection(
                                    selectedValues,
                                    availableValues,
                                )
                                onSelectionChange(
                                    toggleMultiSelection(sanitizedSelection, option.value),
                                )
                                true
                            })
                        }
                    },
                    leadingIcon = {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = null,
                            enabled = optionEnabled,
                            modifier = Modifier
                                .size(CoreUiTheme.sizing.iconMedium)
                                .clearAndSetSemantics { },
                        )
                    },
                )
            }
        }
    }
}

private const val DEFAULT_INLINE_LABEL_LIMIT = 2
