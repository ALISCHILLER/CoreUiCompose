package com.msa.coreui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import com.msa.coreui.foundation.CoreUiTheme

/** Visual hierarchy for a [CoreButton]. */
enum class CoreButtonStyle { PRIMARY, SECONDARY, TERTIARY, DESTRUCTIVE }

enum class CoreButtonSize { COMPACT, DEFAULT, LARGE }

@Immutable
data class CoreButtonContent(
    val label: String,
    val contentDescription: String? = null,
) {
    init {
        require(label.isNotBlank()) { "label must not be blank." }
        require(contentDescription == null || contentDescription.isNotBlank()) {
            "contentDescription must be null or non-blank."
        }
    }
}

/** Standard accessible action button used throughout Core UI. */
@Composable
fun CoreButton(
    content: CoreButtonContent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CoreButtonStyle = CoreButtonStyle.PRIMARY,
    size: CoreButtonSize = CoreButtonSize.DEFAULT,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingDescription: String = CoreUiTheme.strings.loading,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    require(loadingDescription.isNotBlank()) { "loadingDescription must not be blank." }
    val minimumHeight = when (size) {
        CoreButtonSize.COMPACT -> CoreUiTheme.sizing.compactButtonHeight
        CoreButtonSize.DEFAULT -> CoreUiTheme.sizing.buttonHeight
        CoreButtonSize.LARGE -> CoreUiTheme.sizing.largeButtonHeight
    }
    val interactionHeight = maxOf(minimumHeight, CoreUiTheme.sizing.touchTarget)
    val contentPadding = when (size) {
        CoreButtonSize.COMPACT -> PaddingValues(
            horizontal = CoreUiTheme.spacing.md,
            vertical = CoreUiTheme.spacing.sm,
        )
        CoreButtonSize.DEFAULT -> ButtonDefaults.ContentPadding
        CoreButtonSize.LARGE -> PaddingValues(
            horizontal = CoreUiTheme.spacing.xl,
            vertical = CoreUiTheme.spacing.md,
        )
    }
    val buttonModifier = modifier
        .heightIn(min = interactionHeight)
        .semantics {
            role = Role.Button
            content.contentDescription
                ?.takeIf { it != content.label }
                ?.let { this.contentDescription = it }
            if (loading) stateDescription = loadingDescription
        }
    val actualEnabled = enabled && !loading

    val innerContent: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(CoreUiTheme.sizing.iconSmall)
                            .clearAndSetSemantics { },
                        strokeWidth = CoreUiTheme.stroke.strong,
                        color = when (style) {
                            CoreButtonStyle.PRIMARY -> MaterialTheme.colorScheme.onPrimary
                            CoreButtonStyle.DESTRUCTIVE -> MaterialTheme.colorScheme.onError
                            else -> MaterialTheme.colorScheme.primary
                        },
                    )
                    Spacer(Modifier.width(CoreUiTheme.spacing.sm))
                }
                leadingIcon != null -> {
                    Box(Modifier.clearAndSetSemantics { }) { leadingIcon() }
                    Spacer(Modifier.width(CoreUiTheme.spacing.sm))
                }
            }

            Text(text = content.label, style = MaterialTheme.typography.labelLarge)

            if (!loading && trailingIcon != null) {
                Spacer(Modifier.width(CoreUiTheme.spacing.sm))
                Box(Modifier.clearAndSetSemantics { }) { trailingIcon() }
            }
        }
    }

    when (style) {
        CoreButtonStyle.PRIMARY -> Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = actualEnabled,
            contentPadding = contentPadding,
            content = { innerContent() },
        )
        CoreButtonStyle.SECONDARY -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = actualEnabled,
            contentPadding = contentPadding,
            content = { innerContent() },
        )
        CoreButtonStyle.TERTIARY -> TextButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = actualEnabled,
            contentPadding = contentPadding,
            content = { innerContent() },
        )
        CoreButtonStyle.DESTRUCTIVE -> Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = actualEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = CoreUiTheme.alpha.disabledContainer),
                disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = CoreUiTheme.alpha.disabledContent),
            ),
            contentPadding = contentPadding,
            content = { innerContent() },
        )
    }
}

enum class CoreIconButtonStyle { STANDARD, FILLED, OUTLINED, DESTRUCTIVE }

/** Icon-only action. The icon slot should be decorative because semantics are provided here. */
@Composable
fun CoreIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    style: CoreIconButtonStyle = CoreIconButtonStyle.STANDARD,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    val buttonModifier = modifier
        .sizeIn(
            minWidth = CoreUiTheme.sizing.touchTarget,
            minHeight = CoreUiTheme.sizing.touchTarget,
        )
        .semantics { this.contentDescription = contentDescription }

    when (style) {
        CoreIconButtonStyle.STANDARD -> IconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            content = { Box(Modifier.clearAndSetSemantics { }) { content() } },
        )
        CoreIconButtonStyle.OUTLINED -> OutlinedIconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            content = { Box(Modifier.clearAndSetSemantics { }) { content() } },
        )
        CoreIconButtonStyle.FILLED -> FilledIconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            content = { Box(Modifier.clearAndSetSemantics { }) { content() } },
        )
        CoreIconButtonStyle.DESTRUCTIVE -> FilledIconButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(
                    alpha = CoreUiTheme.alpha.disabledContainer,
                ),
                disabledContentColor = MaterialTheme.colorScheme.onError.copy(
                    alpha = CoreUiTheme.alpha.disabledContent,
                ),
            ),
            content = { Box(Modifier.clearAndSetSemantics { }) { content() } },
        )
    }
}
