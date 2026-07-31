package com.msa.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.msa.coreui.foundation.CoreUiTheme
import com.msa.coreui.foundation.CoreUiWindowSizeClass
import com.msa.coreui.foundation.classify

/**
 * Adaptive master-detail container. Compact widths show one pane at a time; larger widths render
 * both panes and keep selection/navigation state owned by the caller.
 */
@Composable
fun CoreResponsivePane(
    showDetailOnCompact: Boolean,
    primary: @Composable (CoreUiWindowSizeClass) -> Unit,
    detail: @Composable (CoreUiWindowSizeClass) -> Unit,
    modifier: Modifier = Modifier,
    primaryWeight: Float = 0.42f,
    detailWeight: Float = 0.58f,
    dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    require(primaryWeight.isFinite() && detailWeight.isFinite()) { "Pane weights must be finite." }
    require(primaryWeight > 0f && detailWeight > 0f) { "Pane weights must be greater than zero." }
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowClass = CoreUiTheme.breakpoints.classify(maxWidth)
        if (windowClass == CoreUiWindowSizeClass.COMPACT) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (showDetailOnCompact) detail(windowClass) else primary(windowClass)
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(primaryWeight).fillMaxSize()) {
                    primary(windowClass)
                }
                VerticalDivider(color = dividerColor)
                Box(modifier = Modifier.weight(detailWeight).fillMaxSize()) {
                    detail(windowClass)
                }
            }
        }
    }
}

/** Responsive section that becomes a two-column row outside compact widths. */
@Composable
fun CoreResponsiveFormRow(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val windowClass = CoreUiTheme.breakpoints.classify(maxWidth)
        if (windowClass == CoreUiWindowSizeClass.COMPACT) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(CoreUiTheme.spacing.md),
            ) {
                first()
                second()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(CoreUiTheme.spacing.lg),
            ) {
                Box(Modifier.weight(1f)) { first() }
                Box(Modifier.weight(1f)) { second() }
            }
        }
    }
}
