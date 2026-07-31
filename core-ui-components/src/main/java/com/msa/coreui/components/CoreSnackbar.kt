package com.msa.coreui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.msa.coreui.foundation.CoreUiTheme

@Immutable
data class CoreSnackbarContent(
    val message: String,
    val actionLabel: String? = null,
    val dismissLabel: String? = null,
    val style: CoreBannerStyle = CoreBannerStyle.INFO,
) {
    init {
        require(message.isNotBlank()) { "message must not be blank." }
        require(actionLabel == null || actionLabel.isNotBlank()) { "actionLabel must be null or non-blank." }
        require(dismissLabel == null || dismissLabel.isNotBlank()) { "dismissLabel must be null or non-blank." }
    }
}

/** Visuals used by [CoreSnackbarHost] and [showCoreSnackbar]. */
@Immutable
class CoreSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val style: CoreBannerStyle = CoreBannerStyle.INFO,
) : SnackbarVisuals {
    init {
        require(message.isNotBlank()) { "message must not be blank." }
        require(actionLabel == null || actionLabel.isNotBlank()) { "actionLabel must be null or non-blank." }
    }
}

/** Shows a styled snackbar without leaking presentation mapping into application code. */
suspend fun SnackbarHostState.showCoreSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
    style: CoreBannerStyle = CoreBannerStyle.INFO,
): SnackbarResult = showSnackbar(
    CoreSnackbarVisuals(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration,
        style = style,
    ),
)

/** Snackbar host that renders [CoreSnackbarVisuals] using the shared semantic status palette. */
@Composable
fun CoreSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val visuals = data.visuals
        val style = (visuals as? CoreSnackbarVisuals)?.style ?: CoreBannerStyle.INFO
        CoreSnackbar(
            content = CoreSnackbarContent(
                message = visuals.message,
                actionLabel = visuals.actionLabel,
                dismissLabel = if (visuals.withDismissAction) CoreUiTheme.strings.dismiss else null,
                style = style,
            ),
            onAction = visuals.actionLabel?.let { { data.performAction() } },
            onDismiss = if (visuals.withDismissAction) ({ data.dismiss() }) else null,
        )
    }
}

/** Snackbar presentation that shares the same semantic status palette as [CoreBanner]. */
@Composable
fun CoreSnackbar(
    content: CoreSnackbarContent,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    require((content.actionLabel == null) == (onAction == null)) {
        "actionLabel and onAction must be provided together."
    }
    require((content.dismissLabel == null) == (onDismiss == null)) {
        "dismissLabel and onDismiss must be provided together."
    }
    val containerColor = when (content.style) {
        CoreBannerStyle.INFO -> CoreUiTheme.statusColors.infoContainer
        CoreBannerStyle.SUCCESS -> CoreUiTheme.statusColors.successContainer
        CoreBannerStyle.WARNING -> CoreUiTheme.statusColors.warningContainer
        CoreBannerStyle.ERROR -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (content.style) {
        CoreBannerStyle.INFO -> CoreUiTheme.statusColors.onInfoContainer
        CoreBannerStyle.SUCCESS -> CoreUiTheme.statusColors.onSuccessContainer
        CoreBannerStyle.WARNING -> CoreUiTheme.statusColors.onWarningContainer
        CoreBannerStyle.ERROR -> MaterialTheme.colorScheme.onErrorContainer
    }

    Snackbar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = contentColor,
        dismissActionContentColor = contentColor,
        action = if (content.actionLabel != null && onAction != null) {
            { TextButton(onClick = onAction) { Text(content.actionLabel) } }
        } else {
            null
        },
        dismissAction = if (content.dismissLabel != null && onDismiss != null) {
            { TextButton(onClick = onDismiss) { Text(content.dismissLabel) } }
        } else {
            null
        },
    ) {
        Text(content.message)
    }
}
