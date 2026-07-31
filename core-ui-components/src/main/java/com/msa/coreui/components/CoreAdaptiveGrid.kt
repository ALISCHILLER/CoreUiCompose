package com.msa.coreui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.coreui.foundation.CoreUiTheme

/** Adaptive lazy grid that keeps card and catalog layouts consistent across phones and tablets. */
@Composable
fun CoreAdaptiveGrid(
    modifier: Modifier = Modifier,
    minimumCellWidth: Dp = CoreUiTheme.sizing.adaptiveGridMinCellWidth,
    contentPadding: PaddingValues = PaddingValues(CoreUiTheme.spacing.lg),
    horizontalSpacing: Dp = CoreUiTheme.spacing.lg,
    verticalSpacing: Dp = CoreUiTheme.spacing.lg,
    content: LazyGridScope.() -> Unit,
) {
    require(minimumCellWidth > 0.dp) { "minimumCellWidth must be greater than zero." }
    require(horizontalSpacing >= 0.dp) { "horizontalSpacing must not be negative." }
    require(verticalSpacing >= 0.dp) { "verticalSpacing must not be negative." }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = minimumCellWidth),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        content = content,
    )
}
