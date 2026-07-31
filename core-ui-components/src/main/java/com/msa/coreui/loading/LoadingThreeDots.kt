package com.msa.coreui.loading

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.coreui.foundation.CoreUiTheme

/** Lightweight, accessible three-dot loading indicator with reduced-motion support. */
@Composable
fun LoadingThreeDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 10.dp,
    gap: Dp = 8.dp,
    contentDescription: String = CoreUiTheme.strings.loading,
) {
    require(dotSize > 0.dp) { "dotSize must be greater than zero." }
    require(gap >= 0.dp) { "gap must not be negative." }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    val scales = if (CoreUiTheme.motion.animationsEnabled) {
        animatedDotScales()
    } else {
        List(DOT_COUNT) { rememberUpdatedState(1f) }
    }

    Row(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            liveRegion = LiveRegionMode.Polite
        },
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        scales.forEach { scale ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(scale.value)
                    .background(color = color, shape = CircleShape),
            )
        }
    }
}

@Composable
private fun animatedDotScales(): List<State<Float>> {
    val transition = rememberInfiniteTransition(label = "Three-dot loading")
    val duration = CoreUiTheme.motion.slow.coerceAtLeast(MIN_ANIMATION_DURATION_MS)
    return List(DOT_COUNT) { index ->
        transition.animateFloat(
            initialValue = MIN_SCALE,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = duration
                    MIN_SCALE at 0
                    1f at ((duration * 0.2f) + index * (duration * 0.13f)).toInt() with FastOutSlowInEasing
                    MIN_SCALE at ((duration * 0.47f) + index * (duration * 0.13f)).toInt()
                    MIN_SCALE at duration
                },
                repeatMode = RepeatMode.Restart,
            ),
            label = "Loading dot ${index + 1}",
        )
    }
}

/** Source-compatible migration alias for the original prototype API. */
@Deprecated(
    message = "Use LoadingThreeDots.",
    replaceWith = ReplaceWith("LoadingThreeDots(modifier, circleColor, circleSize, spaceBetween, accessibilityLabel)"),
)
@Composable
fun LoadingThreePoints(
    modifier: Modifier = Modifier,
    circleSize: Dp = 10.dp,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 8.dp,
    @Suppress("UNUSED_PARAMETER") travelDistance: Dp = 10.dp,
    accessibilityLabel: String = CoreUiTheme.strings.loading,
) {
    LoadingThreeDots(
        modifier = modifier,
        color = circleColor,
        dotSize = circleSize,
        gap = spaceBetween,
        contentDescription = accessibilityLabel,
    )
}

private const val DOT_COUNT = 3
private const val MIN_SCALE = 0.62f
private const val MIN_ANIMATION_DURATION_MS = 300
