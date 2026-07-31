package com.msa.coreui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.coreui.foundation.CoreUiTheme
import java.util.Locale

enum class CoreAvatarStyle { PRIMARY, NEUTRAL }
enum class CoreAvatarStatus { NONE, ONLINE, AWAY, BUSY, OFFLINE }

/** Accessible circular avatar with image fallback and an optional presence indicator. */
@Composable
fun CoreAvatar(
    label: String,
    modifier: Modifier = Modifier,
    contentDescription: String = label,
    size: Dp = CoreUiTheme.sizing.avatarMedium,
    style: CoreAvatarStyle = CoreAvatarStyle.PRIMARY,
    status: CoreAvatarStatus = CoreAvatarStatus.NONE,
    statusContentDescription: String? = null,
    image: (@Composable BoxScope.() -> Unit)? = null,
) {
    require(label.isNotBlank()) { "label must not be blank." }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    require(size > 0.dp) { "size must be greater than zero." }
    require(statusContentDescription == null || statusContentDescription.isNotBlank()) {
        "statusContentDescription must be null or non-blank."
    }

    val (containerColor, contentColor) = when (style) {
        CoreAvatarStyle.PRIMARY ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        CoreAvatarStyle.NEUTRAL ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    val resolvedStatusDescription = statusContentDescription ?: when (status) {
        CoreAvatarStatus.NONE -> null
        CoreAvatarStatus.ONLINE -> CoreUiTheme.strings.online
        CoreAvatarStatus.AWAY -> CoreUiTheme.strings.away
        CoreAvatarStatus.BUSY -> CoreUiTheme.strings.busy
        CoreAvatarStatus.OFFLINE -> CoreUiTheme.strings.offline
    }
    val semanticsLabel = listOfNotNull(contentDescription, resolvedStatusDescription).joinToString(", ")

    Box(
        modifier = modifier
            .size(size)
            .clearAndSetSemantics { this.contentDescription = semanticsLabel },
    ) {
        Surface(
            modifier = Modifier.matchParentSize().clip(CircleShape),
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (image == null) {
                    Text(
                        text = avatarInitials(label),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                } else {
                    image()
                }
            }
        }
        if (status != CoreAvatarStatus.NONE) {
            val indicatorColor = when (status) {
                CoreAvatarStatus.ONLINE -> CoreUiTheme.statusColors.success
                CoreAvatarStatus.AWAY -> CoreUiTheme.statusColors.warning
                CoreAvatarStatus.BUSY -> MaterialTheme.colorScheme.error
                CoreAvatarStatus.OFFLINE -> MaterialTheme.colorScheme.outline
                CoreAvatarStatus.NONE -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * AVATAR_STATUS_RATIO)
                    .clip(CircleShape)
                    .background(indicatorColor)
                    .border(CoreUiTheme.stroke.strong, MaterialTheme.colorScheme.surface, CircleShape),
            )
        }
    }
}

internal fun avatarInitials(label: String): String {
    val parts = label.trim().split(Regex("\\s+")).filter(String::isNotBlank)
    if (parts.isEmpty()) return ""
    val first = parts.first().firstCodePoint().uppercase(Locale.getDefault())
    val last = parts.takeIf { it.size > 1 }?.last()?.firstCodePoint()?.uppercase(Locale.getDefault())
    return if (last == null) first else first + last
}

private fun String.firstCodePoint(): String =
    String(Character.toChars(codePointAt(0)))

private const val AVATAR_STATUS_RATIO = 0.28f
