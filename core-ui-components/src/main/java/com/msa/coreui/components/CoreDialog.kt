package com.msa.coreui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.DialogProperties

/** Standard confirm/cancel dialog with Core UI buttons and caller-owned dismissal behavior. */
@Composable
fun CoreAlertDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String? = null,
    destructive: Boolean = false,
    confirmEnabled: Boolean = true,
    dismissEnabled: Boolean = true,
    confirmLoading: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    allowDismissWhileLoading: Boolean = false,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(message.isNotBlank()) { "message must not be blank." }
    require(confirmLabel.isNotBlank()) { "confirmLabel must not be blank." }
    require(dismissLabel == null || dismissLabel.isNotBlank()) { "dismissLabel must be null or non-blank." }

    AlertDialog(
        onDismissRequest = {
            if (dismissEnabled && (!confirmLoading || allowDismissWhileLoading)) onDismissRequest()
        },
        modifier = modifier,
        icon = icon,
        title = { Text(title, modifier = Modifier.semantics { heading() }) },
        text = { Text(message) },
        confirmButton = {
            CoreButton(
                content = CoreButtonContent(confirmLabel),
                onClick = onConfirm,
                enabled = confirmEnabled && !confirmLoading,
                loading = confirmLoading,
                style = if (destructive) CoreButtonStyle.DESTRUCTIVE else CoreButtonStyle.PRIMARY,
            )
        },
        dismissButton = dismissLabel?.let { label ->
            {
                CoreButton(
                    content = CoreButtonContent(label),
                    onClick = onDismissRequest,
                    enabled = dismissEnabled && (!confirmLoading || allowDismissWhileLoading),
                    style = CoreButtonStyle.TERTIARY,
                )
            }
        },
        properties = properties,
    )
}

/**
 * Slot-based dialog for forms and rich confirmation content while retaining Core UI validation,
 * heading semantics, and caller-owned actions.
 */
@Composable
fun CoreContentDialog(
    title: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    dismissEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    require(title.isNotBlank()) { "title must not be blank." }

    AlertDialog(
        onDismissRequest = { if (dismissEnabled) onDismissRequest() },
        modifier = modifier,
        icon = icon,
        title = { Text(title, modifier = Modifier.semantics { heading() }) },
        text = content,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        properties = properties,
    )
}
