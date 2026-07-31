package com.msa.coreui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.msa.coreui.foundation.CoreUiTheme

enum class CoreChipStyle { NEUTRAL, INFO, SUCCESS, WARNING, ERROR }

/** Compact action or filter chip with semantic status colors and an explicit selected state. */
@Composable
fun CoreChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: CoreChipStyle = CoreChipStyle.NEUTRAL,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    require(label.isNotBlank()) { "label must not be blank." }
    val status = CoreUiTheme.statusColors
    val containerColor: Color
    val contentColor: Color
    val borderColor: Color
    when (style) {
        CoreChipStyle.NEUTRAL -> {
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
            contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            borderColor = MaterialTheme.colorScheme.outline
        }
        CoreChipStyle.INFO -> {
            containerColor = status.infoContainer
            contentColor = status.onInfoContainer
            borderColor = status.info
        }
        CoreChipStyle.SUCCESS -> {
            containerColor = status.successContainer
            contentColor = status.onSuccessContainer
            borderColor = status.success
        }
        CoreChipStyle.WARNING -> {
            containerColor = status.warningContainer
            contentColor = status.onWarningContainer
            borderColor = status.warning
        }
        CoreChipStyle.ERROR -> {
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
            borderColor = MaterialTheme.colorScheme.error
        }
    }
    val alpha = if (enabled) 1f else CoreUiTheme.alpha.disabledContent
    val shape = RoundedCornerShape(CoreUiTheme.radii.full)

    Row(
        modifier = modifier
            .sizeIn(
                minWidth = CoreUiTheme.sizing.touchTarget,
                minHeight = CoreUiTheme.sizing.touchTarget,
            )
            .clip(shape)
            .background(containerColor.copy(alpha = containerColor.alpha * alpha), shape)
            .border(BorderStroke(CoreUiTheme.stroke.strong, borderColor.copy(alpha = alpha)), shape)
            .semantics {
                role = Role.Button
                this.selected = selected
                if (!enabled) disabled()
            }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = CoreUiTheme.spacing.md, vertical = CoreUiTheme.spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Box(Modifier.clearAndSetSemantics { }) { leadingIcon() }
            Spacer(Modifier.width(CoreUiTheme.spacing.sm))
        }
        Text(
            text = label,
            color = contentColor.copy(alpha = alpha),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

/** Input chip with a dedicated accessible remove action. */
@Composable
fun CoreInputChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: CoreChipStyle = CoreChipStyle.NEUTRAL,
    leadingIcon: (@Composable () -> Unit)? = null,
    removeContentDescription: String = "${CoreUiTheme.strings.close}: $label",
) {
    require(label.isNotBlank()) { "label must not be blank." }
    require(removeContentDescription.isNotBlank()) { "removeContentDescription must not be blank." }
    val status = CoreUiTheme.statusColors
    val (containerColor, contentColor, borderColor) = when (style) {
        CoreChipStyle.NEUTRAL -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outlineVariant,
        )
        CoreChipStyle.INFO -> Triple(status.infoContainer, status.onInfoContainer, status.info)
        CoreChipStyle.SUCCESS -> Triple(status.successContainer, status.onSuccessContainer, status.success)
        CoreChipStyle.WARNING -> Triple(status.warningContainer, status.onWarningContainer, status.warning)
        CoreChipStyle.ERROR -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.error,
        )
    }
    val alpha = if (enabled) 1f else CoreUiTheme.alpha.disabledContent
    val shape = RoundedCornerShape(CoreUiTheme.radii.full)

    Row(
        modifier = modifier
            .heightIn(min = CoreUiTheme.sizing.touchTarget)
            .clip(shape)
            .background(containerColor.copy(alpha = containerColor.alpha * alpha), shape)
            .border(BorderStroke(CoreUiTheme.stroke.hairline, borderColor.copy(alpha = alpha)), shape)
            .padding(start = CoreUiTheme.spacing.md, end = CoreUiTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Box(Modifier.clearAndSetSemantics { }) { leadingIcon() }
            Spacer(Modifier.width(CoreUiTheme.spacing.sm))
        }
        Text(
            text = label,
            color = contentColor.copy(alpha = alpha),
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.width(CoreUiTheme.spacing.xs))
        CoreIconButton(
            onClick = onRemove,
            contentDescription = removeContentDescription,
            enabled = enabled,
        ) {
            CoreIcon(
                imageVector = CoreIcons.Close,
                contentDescription = null,
                decorative = true,
            )
        }
    }
}
