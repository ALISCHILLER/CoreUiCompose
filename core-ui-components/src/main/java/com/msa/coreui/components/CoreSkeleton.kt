package com.msa.coreui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.coreui.foundation.CoreUiTheme

/** Decorative loading placeholder that automatically respects the Core UI reduced-motion policy. */
@Composable
fun CoreSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val alpha = if (CoreUiTheme.motion.animationsEnabled) {
        val transition = rememberInfiniteTransition(label = "CoreSkeleton")
        transition.animateFloat(
            initialValue = 0.45f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = maxOf(CoreUiTheme.motion.slow, 1)),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "CoreSkeletonAlpha",
        ).value
    } else {
        0.65f
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = alpha))
            .clearAndSetSemantics { },
    )
}

/** Multi-line text placeholder with a shorter final line. */
@Composable
fun CoreSkeletonText(
    lines: Int,
    modifier: Modifier = Modifier,
    lineHeight: Dp = CoreUiTheme.sizing.skeletonLineHeight,
) {
    require(lines > 0) { "lines must be greater than zero." }
    require(lineHeight > 0.dp) { "lineHeight must be greater than zero." }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.sm),
    ) {
        repeat(lines) { index ->
            CoreSkeleton(
                modifier = Modifier
                    .fillMaxWidth(if (index == lines - 1 && lines > 1) 0.68f else 1f)
                    .height(lineHeight),
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
    }
}

/** Circular avatar placeholder. */
@Composable
fun CoreSkeletonAvatar(
    modifier: Modifier = Modifier,
    size: Dp = CoreUiTheme.sizing.avatarMedium,
) {
    require(size > 0.dp) { "size must be greater than zero." }
    CoreSkeleton(
        modifier = modifier.size(size),
        shape = CircleShape,
    )
}
