package com.msa.coreui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.LocalContentColor
import com.msa.coreui.foundation.CoreUiTheme

enum class CoreBannerStyle { INFO, SUCCESS, WARNING, ERROR }

enum class CoreStateStyle { NEUTRAL, SUCCESS, ERROR, OFFLINE }

/** Inline live-region status message with semantic colors and optional actions. */
@Composable
fun CoreBanner(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    style: CoreBannerStyle = CoreBannerStyle.INFO,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(message == null || message.isNotBlank()) { "message must be null or non-blank." }
    require(actionLabel == null || actionLabel.isNotBlank()) { "actionLabel must be null or non-blank." }
    require(dismissLabel == null || dismissLabel.isNotBlank()) { "dismissLabel must be null or non-blank." }
    require((actionLabel == null) == (onAction == null)) { "actionLabel and onAction must be provided together." }
    require((dismissLabel == null) == (onDismiss == null)) { "dismissLabel and onDismiss must be provided together." }
    val colors = when (style) {
        CoreBannerStyle.INFO -> Triple(
            CoreUiTheme.statusColors.infoContainer,
            CoreUiTheme.statusColors.onInfoContainer,
            CoreUiTheme.statusColors.info,
        )
        CoreBannerStyle.SUCCESS -> Triple(
            CoreUiTheme.statusColors.successContainer,
            CoreUiTheme.statusColors.onSuccessContainer,
            CoreUiTheme.statusColors.success,
        )
        CoreBannerStyle.WARNING -> Triple(
            CoreUiTheme.statusColors.warningContainer,
            CoreUiTheme.statusColors.onWarningContainer,
            CoreUiTheme.statusColors.warning,
        )
        CoreBannerStyle.ERROR -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.error,
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        color = colors.first,
        contentColor = colors.second,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(CoreUiTheme.stroke.strong, colors.third.copy(alpha = CoreUiTheme.alpha.outline)),
    ) {
        Row(
            modifier = Modifier.padding(CoreUiTheme.spacing.lg),
            verticalAlignment = Alignment.Top,
        ) {
            if (icon != null) {
                Box(Modifier.clearAndSetSemantics { }) { icon() }
                Spacer(Modifier.width(CoreUiTheme.spacing.md))
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
            ) {
                Text(
                    title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleSmall,
                )
                if (message != null) Text(message, style = MaterialTheme.typography.bodyMedium)
                if ((actionLabel != null && onAction != null) || (dismissLabel != null && onDismiss != null)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm)) {
                        if (actionLabel != null && onAction != null) {
                            TextButton(onClick = onAction) { Text(actionLabel, color = colors.third) }
                        }
                        if (dismissLabel != null && onDismiss != null) {
                            TextButton(onClick = onDismiss) { Text(dismissLabel, color = colors.third) }
                        }
                    }
                }
            }
        }
    }
}

/** Centered empty/error/success state for full screens and bounded containers. */
@Composable
fun CoreStateView(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    style: CoreStateStyle = CoreStateStyle.NEUTRAL,
    accessibilityLabel: String? = null,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(message == null || message.isNotBlank()) { "message must be null or non-blank." }
    require(actionLabel == null || actionLabel.isNotBlank()) { "actionLabel must be null or non-blank." }
    require((actionLabel == null) == (onAction == null)) { "actionLabel and onAction must be provided together." }
    require(accessibilityLabel == null || accessibilityLabel.isNotBlank()) {
        "accessibilityLabel must be null or non-blank."
    }
    val accentColor = when (style) {
        CoreStateStyle.NEUTRAL -> MaterialTheme.colorScheme.primary
        CoreStateStyle.SUCCESS -> CoreUiTheme.statusColors.success
        CoreStateStyle.ERROR -> MaterialTheme.colorScheme.error
        CoreStateStyle.OFFLINE -> CoreUiTheme.statusColors.warning
    }
    Column(
        modifier = modifier
            .semantics {
                accessibilityLabel?.let { stateDescription = it }
                if (style == CoreStateStyle.ERROR || style == CoreStateStyle.OFFLINE) {
                    liveRegion = LiveRegionMode.Polite
                }
            }
            .padding(CoreUiTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.md),
    ) {
        if (icon != null) {
            CompositionLocalProvider(LocalContentColor provides accentColor) {
                Box(
                    Modifier
                        .size(CoreUiTheme.sizing.touchTarget + CoreUiTheme.spacing.sm)
                        .clearAndSetSemantics { },
                    contentAlignment = Alignment.Center,
                ) { icon() }
            }
        }
        Text(
            title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        if (message != null) {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (actionLabel != null && onAction != null) {
            CoreButton(CoreButtonContent(actionLabel), onAction)
        }
    }
}

/** Indeterminate loading indicator with accessible progress semantics. */
@Composable
fun CoreLoadingIndicator(
    modifier: Modifier = Modifier,
    label: String? = null,
    accessibilityLabel: String = label ?: CoreUiTheme.strings.loading,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    require(label == null || label.isNotBlank()) { "label must be null or non-blank." }
    require(accessibilityLabel.isNotBlank()) { "accessibilityLabel must not be blank." }
    Column(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = accessibilityLabel
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            liveRegion = LiveRegionMode.Polite
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.md),
    ) {
        CircularProgressIndicator(color = color)
        if (label != null) Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
