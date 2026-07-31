package com.msa.coreui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import com.msa.coreui.foundation.CoreUiTheme

/**
 * Controlled pagination surface with first/previous/next/last actions and a bounded page window.
 * Page numbers are one-based.
 */
@Composable
fun CorePagination(
    currentPage: Int,
    pageCount: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisiblePages: Int = 5,
    showBoundaryActions: Boolean = true,
) {
    require(pageCount > 0) { "pageCount must be greater than zero." }
    require(currentPage in 1..pageCount) { "currentPage must be inside 1..pageCount." }
    require(maxVisiblePages >= 1) { "maxVisiblePages must be at least one." }

    val visiblePages = calculateVisiblePages(currentPage, pageCount, maxVisiblePages)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val previousIcon = if (rtl) CoreIcons.ChevronRight else CoreIcons.ChevronLeft
    val nextIcon = if (rtl) CoreIcons.ChevronLeft else CoreIcons.ChevronRight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBoundaryActions && visiblePages.first > 1) {
            CoreButton(
                content = CoreButtonContent(CoreUiTheme.strings.formatNumber(1), CoreUiTheme.strings.firstPage),
                onClick = { onPageSelected(1) },
                style = CoreButtonStyle.TERTIARY,
                size = CoreButtonSize.COMPACT,
            )
            if (visiblePages.first > 2) Text("…", modifier = Modifier.clearAndSetSemantics { })
        }
        CoreIconButton(
            onClick = { onPageSelected(currentPage - 1) },
            contentDescription = CoreUiTheme.strings.previous,
            enabled = currentPage > 1,
        ) {
            CoreIcon(previousIcon, contentDescription = null, decorative = true)
        }

        visiblePages.forEach { page ->
            val isCurrent = page == currentPage
            val pageDescription = CoreUiTheme.strings.pageOf(page, pageCount)
            CoreButton(
                content = CoreButtonContent(
                    label = CoreUiTheme.strings.formatNumber(page),
                    contentDescription = pageDescription,
                ),
                onClick = { onPageSelected(page) },
                style = if (isCurrent) CoreButtonStyle.PRIMARY else CoreButtonStyle.TERTIARY,
                size = CoreButtonSize.COMPACT,
                modifier = Modifier.semantics {
                    role = Role.Tab
                    selected = isCurrent
                    contentDescription = pageDescription
                },
            )
        }

        CoreIconButton(
            onClick = { onPageSelected(currentPage + 1) },
            contentDescription = CoreUiTheme.strings.next,
            enabled = currentPage < pageCount,
        ) {
            CoreIcon(nextIcon, contentDescription = null, decorative = true)
        }
        if (showBoundaryActions && visiblePages.last < pageCount) {
            if (visiblePages.last < pageCount - 1) Text("…", modifier = Modifier.clearAndSetSemantics { })
            CoreButton(
                content = CoreButtonContent(CoreUiTheme.strings.formatNumber(pageCount), CoreUiTheme.strings.lastPage),
                onClick = { onPageSelected(pageCount) },
                style = CoreButtonStyle.TERTIARY,
                size = CoreButtonSize.COMPACT,
            )
        }
    }
}
