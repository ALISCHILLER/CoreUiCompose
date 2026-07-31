package com.msa.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow

@Immutable
data class CoreTabItem(
    val label: String,
    val contentDescription: String = label,
    val enabled: Boolean = true,
) {
    init {
        require(label.isNotBlank()) { "label must not be blank." }
        require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    }
}

/**
 * Controlled primary tabs with validation, accessibility labels, optional icons, and optional
 * horizontal scrolling. More than [maxFixedTabs] destinations automatically use a scrollable row
 * to prevent truncated labels and undersized touch targets. Selection remains owned by the caller.
 */
@Composable
fun CoreTabs(
    items: List<CoreTabItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    maxFixedTabs: Int = 4,
    icon: (@Composable (item: CoreTabItem, selected: Boolean) -> Unit)? = null,
) {
    require(items.size >= 2) { "Tabs require at least two items." }
    require(maxFixedTabs >= 2) { "maxFixedTabs must be at least two." }
    require(selectedIndex in items.indices) { "selectedIndex must point to an existing item." }
    require(items[selectedIndex].enabled) { "The selected tab must be enabled." }

    val tabs: @Composable () -> Unit = {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Tab(
                selected = selected,
                onClick = { onSelected(index) },
                enabled = item.enabled,
                modifier = Modifier.semantics {
                    if (item.contentDescription != item.label) {
                        contentDescription = item.contentDescription
                    }
                },
                text = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                icon = icon?.let { iconContent ->
                    {
                        Box(Modifier.clearAndSetSemantics { }) {
                            iconContent(item, selected)
                        }
                    }
                },
            )
        }
    }

    if (shouldUseScrollableTabs(items.size, scrollable, maxFixedTabs)) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier.fillMaxWidth(),
            tabs = tabs,
        )
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier.fillMaxWidth(),
            tabs = tabs,
        )
    }
}
