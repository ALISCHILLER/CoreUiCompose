package com.msa.coreui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.msa.coreui.foundation.CoreUiTheme

/** Surface hierarchy for [CoreStyledCard]. */
enum class CoreCardStyle { FILLED, ELEVATED, OUTLINED }

/** Backward-compatible outlined card. */
@Composable
fun CoreCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable () -> Unit,
) {
    CoreStyledCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        style = CoreCardStyle.OUTLINED,
        content = content,
    )
}

/** Card surface supporting filled, elevated, and outlined hierarchy. */
@Composable
fun CoreStyledCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.large,
    style: CoreCardStyle = CoreCardStyle.OUTLINED,
    containerColor: Color = when (style) {
        CoreCardStyle.FILLED -> MaterialTheme.colorScheme.surfaceVariant
        CoreCardStyle.ELEVATED, CoreCardStyle.OUTLINED -> MaterialTheme.colorScheme.surface
    },
    content: @Composable () -> Unit,
) {
    val contentColor = contentColorFor(containerColor)
    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = containerColor.copy(alpha = CoreUiTheme.alpha.disabledContainer),
        disabledContentColor = contentColor.copy(alpha = CoreUiTheme.alpha.disabledContent),
    )
    val staticColors = if (enabled) {
        colors
    } else {
        CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = CoreUiTheme.alpha.disabledContainer),
            contentColor = contentColor.copy(alpha = CoreUiTheme.alpha.disabledContent),
        )
    }
    val staticModifier = modifier.then(
        if (enabled) Modifier else Modifier.semantics { disabled() },
    )
    when (style) {
        CoreCardStyle.FILLED -> if (onClick == null) {
            Card(
                modifier = staticModifier,
                shape = shape,
                colors = staticColors,
                content = { content() },
            )
        } else {
            Card(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = shape,
                colors = colors,
                content = { content() },
            )
        }

        CoreCardStyle.ELEVATED -> {
            val elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = CoreUiTheme.elevation.level2,
                pressedElevation = CoreUiTheme.elevation.level1,
                focusedElevation = CoreUiTheme.elevation.level3,
                hoveredElevation = CoreUiTheme.elevation.level3,
                disabledElevation = CoreUiTheme.elevation.level0,
            )
            if (onClick == null) {
                ElevatedCard(
                    modifier = staticModifier,
                    shape = shape,
                    colors = staticColors,
                    elevation = elevation,
                    content = { content() },
                )
            } else {
                ElevatedCard(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    colors = colors,
                    elevation = elevation,
                    content = { content() },
                )
            }
        }

        CoreCardStyle.OUTLINED -> {
            val border = BorderStroke(
                CoreUiTheme.stroke.hairline,
                MaterialTheme.colorScheme.outlineVariant,
            )
            if (onClick == null) {
                OutlinedCard(
                    modifier = staticModifier,
                    shape = shape,
                    colors = staticColors,
                    border = border,
                    content = { content() },
                )
            } else {
                OutlinedCard(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    colors = colors,
                    border = border,
                    content = { content() },
                )
            }
        }
    }
}

/** Flexible accessible list row used for settings, menus, navigation, and summaries. */
@Composable
fun CoreListItem(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    contentDescription: String? = null,
    role: Role = Role.Button,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(supportingText == null || supportingText.isNotBlank()) {
        "supportingText must be null or non-blank."
    }
    require(contentDescription == null || contentDescription.isNotBlank()) {
        "contentDescription must be null or non-blank."
    }
    val shape = MaterialTheme.shapes.medium
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
    val titleColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = CoreUiTheme.alpha.disabledContent)
        selected -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    val supportingColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = CoreUiTheme.alpha.disabledContent)
        selected -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = CoreUiTheme.alpha.secondaryContent)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val rowModifier = modifier
        .clip(shape)
        .background(containerColor, shape)
        .semantics {
            if (selected) this.selected = true
            contentDescription?.let { this.contentDescription = it }
            if (onClick != null) this.role = role
            if (!enabled) disabled()
        }
        .then(
            if (onClick == null) {
                Modifier
            } else {
                Modifier.clickable(enabled = enabled, role = role, onClick = onClick)
            },
        )

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .heightIn(min = CoreUiTheme.sizing.listItemMinHeight)
            .padding(horizontal = CoreUiTheme.spacing.lg, vertical = CoreUiTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(CoreUiTheme.spacing.md))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = titleColor)
            if (supportingText != null) {
                Text(supportingText, style = MaterialTheme.typography.bodyMedium, color = supportingColor)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(CoreUiTheme.spacing.md))
            trailing()
        }
    }
}
