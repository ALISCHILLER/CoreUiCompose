package com.msa.coreui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.msa.coreui.foundation.CoreUiTheme

enum class CoreBadgeStyle { NEUTRAL, INFO, SUCCESS, WARNING, ERROR }

/** Compact semantic label for statuses, counts, and short metadata. */
@Composable
fun CoreBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: CoreBadgeStyle = CoreBadgeStyle.NEUTRAL,
    contentDescription: String = text,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = CoreUiTheme.spacing.sm,
        vertical = CoreUiTheme.spacing.xxs,
    ),
) {
    require(text.isNotBlank()) { "text must not be blank." }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }

    val (containerColor, contentColor) = badgeColors(style)
    Surface(
        modifier = modifier.clearAndSetSemantics { this.contentDescription = contentDescription },
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .defaultMinSize(minHeight = CoreUiTheme.sizing.badgeMinHeight)
                .padding(contentPadding),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

@Composable
private fun badgeColors(style: CoreBadgeStyle): Pair<Color, Color> = when (style) {
    CoreBadgeStyle.NEUTRAL ->
        MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    CoreBadgeStyle.INFO ->
        CoreUiTheme.statusColors.infoContainer to CoreUiTheme.statusColors.onInfoContainer
    CoreBadgeStyle.SUCCESS ->
        CoreUiTheme.statusColors.successContainer to CoreUiTheme.statusColors.onSuccessContainer
    CoreBadgeStyle.WARNING ->
        CoreUiTheme.statusColors.warningContainer to CoreUiTheme.statusColors.onWarningContainer
    CoreBadgeStyle.ERROR ->
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
}
