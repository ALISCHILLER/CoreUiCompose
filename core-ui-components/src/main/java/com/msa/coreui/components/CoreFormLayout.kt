package com.msa.coreui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.coreui.foundation.CoreUiTheme

/**
 * Groups related fields under a semantic heading and optional supporting copy.
 *
 * This primitive intentionally owns spacing rather than validation state, keeping business rules in
 * the consuming screen while ensuring long forms remain visually and semantically structured.
 */
@Composable
fun CoreFormSection(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    showDivider: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(description == null || description.isNotBlank()) {
        "description must be null or non-blank."
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs)) {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        content()
        if (showDivider) HorizontalDivider()
    }
}

/**
 * Places a primary and optional secondary action side by side when space permits and stacks them on
 * narrow widths. The primary action is shown first when stacked, preserving visual priority without
 * forcing localized labels into cramped rows.
 */
@Composable
fun CoreResponsiveActions(
    primaryAction: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    secondaryAction: (@Composable () -> Unit)? = null,
    stackBelowWidth: Dp = CoreUiTheme.sizing.actionStackBreakpoint,
    primaryFirstWhenStacked: Boolean = true,
) {
    require(stackBelowWidth > 0.dp) { "stackBelowWidth must be greater than zero." }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < stackBelowWidth) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
            ) {
                if (primaryFirstWhenStacked) {
                    Box(Modifier.fillMaxWidth()) { primaryAction() }
                    if (secondaryAction != null) Box(Modifier.fillMaxWidth()) { secondaryAction() }
                } else {
                    if (secondaryAction != null) Box(Modifier.fillMaxWidth()) { secondaryAction() }
                    Box(Modifier.fillMaxWidth()) { primaryAction() }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (secondaryAction != null) {
                    secondaryAction()
                    Spacer(Modifier.width(CoreUiTheme.spacing.sm))
                }
                primaryAction()
            }
        }
    }
}
