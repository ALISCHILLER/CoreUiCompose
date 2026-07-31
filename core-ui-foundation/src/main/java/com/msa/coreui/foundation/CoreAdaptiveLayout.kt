package com.msa.coreui.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centers content, applies consistent responsive gutters, and reports the active Core UI width class.
 */
@Composable
fun CoreContentContainer(
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = CoreUiTheme.sizing.contentMaxWidth,
    compactPadding: PaddingValues = PaddingValues(horizontal = CoreUiTheme.spacing.lg),
    expandedPadding: PaddingValues = PaddingValues(horizontal = CoreUiTheme.spacing.xl),
    content: @Composable (CoreUiWindowSizeClass) -> Unit,
) {
    require(maxContentWidth > 0.dp) { "maxContentWidth must be greater than zero." }
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val windowClass = CoreUiTheme.breakpoints.classify(maxWidth)
        val padding = if (windowClass == CoreUiWindowSizeClass.COMPACT) {
            compactPadding
        } else {
            expandedPadding
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(modifier = Modifier.widthIn(max = maxContentWidth).fillMaxWidth()) {
                content(windowClass)
            }
        }
    }
}
