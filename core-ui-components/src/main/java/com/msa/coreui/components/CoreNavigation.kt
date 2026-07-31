package com.msa.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.msa.coreui.foundation.CoreUiTheme
import com.msa.coreui.foundation.CoreUiWindowSizeClass
import com.msa.coreui.foundation.classify

@Immutable
data class CoreNavigationItem(
    val label: String,
    val contentDescription: String = label,
    val enabled: Boolean = true,
    val badgeText: String? = null,
    val badgeContentDescription: String? = badgeText,
) {
    init {
        require(label.isNotBlank()) { "label must not be blank." }
        require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
        require(badgeText == null || badgeText.isNotBlank()) { "badgeText must be null or non-blank." }
        require(badgeContentDescription == null || badgeContentDescription.isNotBlank()) {
            "badgeContentDescription must be null or non-blank."
        }
    }
}

/** Bottom navigation surface for compact layouts. */
@Composable
fun CoreNavigationBar(
    items: List<CoreNavigationItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    icon: @Composable (item: CoreNavigationItem, selected: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    alwaysShowLabels: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    validateNavigation(items, selectedIndex)
    validateBottomNavigationItemCount(items.size)
    NavigationBar(
        modifier = modifier.heightIn(min = CoreUiTheme.sizing.navigationBarHeight),
        containerColor = containerColor,
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedIndex == index
            NavigationBarItem(
                selected = selected,
                onClick = { onSelected(index) },
                enabled = item.enabled,
                icon = { NavigationIcon(item, selected, icon) },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                alwaysShowLabel = alwaysShowLabels,
                modifier = Modifier.then(item.semanticsModifier()),
            )
        }
    }
}

/** Side navigation surface for medium and expanded layouts. */
@Composable
fun CoreNavigationRail(
    items: List<CoreNavigationItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    icon: @Composable (item: CoreNavigationItem, selected: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    validateNavigation(items, selectedIndex)
    NavigationRail(
        modifier = modifier.width(CoreUiTheme.sizing.navigationRailWidth),
        header = header?.let { content -> { content() } },
        containerColor = containerColor,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            items.forEachIndexed { index, item ->
                val selected = selectedIndex == index
                NavigationRailItem(
                    selected = selected,
                    onClick = { onSelected(index) },
                    enabled = item.enabled,
                    icon = { NavigationIcon(item, selected, icon) },
                    label = {
                        Text(
                            text = item.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    modifier = Modifier.then(item.semanticsModifier()),
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(
    item: CoreNavigationItem,
    selected: Boolean,
    icon: @Composable (item: CoreNavigationItem, selected: Boolean) -> Unit,
) {
    val iconContent: @Composable () -> Unit = {
        Box(Modifier.clearAndSetSemantics { }) {
            icon(item, selected)
        }
    }
    if (item.badgeText == null) {
        iconContent()
    } else {
        BadgedBox(
            badge = {
                Badge(
                    modifier = Modifier.clearAndSetSemantics {
                        item.badgeContentDescription?.let { contentDescription = it }
                    },
                ) {
                    Text(item.badgeText)
                }
            },
        ) {
            iconContent()
        }
    }
}


private fun validateNavigation(items: List<CoreNavigationItem>, selectedIndex: Int) {
    require(items.isNotEmpty()) { "items must not be empty." }
    require(selectedIndex in items.indices) { "selectedIndex must point to an existing item." }
    require(items[selectedIndex].enabled) { "The selected navigation item must be enabled." }
}

private fun CoreNavigationItem.semanticsModifier(): Modifier =
    if (contentDescription == label) {
        Modifier
    } else {
        Modifier.semantics { contentDescription = this@semanticsModifier.contentDescription }
    }

/**
 * Responsive screen shell: compact widths use bottom navigation when two to five destinations are
 * present. Larger destination sets automatically use a scrollable rail to avoid cramped labels and
 * width-dependent crashes. Selection and navigation remain fully controlled by the caller.
 */
@Composable
fun CoreAdaptiveNavigationScaffold(
    title: String,
    items: List<CoreNavigationItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    icon: @Composable (item: CoreNavigationItem, selected: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigation: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    railHeader: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    alwaysShowBottomLabels: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBarStyle: CoreTopBarStyle = CoreTopBarStyle.SMALL,
    content: @Composable (PaddingValues, CoreUiWindowSizeClass) -> Unit,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(subtitle == null || subtitle.isNotBlank()) { "subtitle must be null or non-blank." }
    validateNavigation(items, selectedIndex)
    require(items.size >= 2) { "Adaptive navigation requires at least two items." }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowClass = CoreUiTheme.breakpoints.classify(maxWidth)
        val useRail = shouldUseNavigationRail(windowClass, items.size)
        if (!useRail) {
            validateBottomNavigationItemCount(items.size)
            CoreScaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    CoreTopBar(
                        title = title,
                        subtitle = subtitle,
                        navigation = navigation,
                        actions = actions,
                        style = topBarStyle,
                    )
                },
                bottomBar = {
                    CoreNavigationBar(
                        items = items,
                        selectedIndex = selectedIndex,
                        onSelected = onSelected,
                        icon = icon,
                        alwaysShowLabels = alwaysShowBottomLabels,
                    )
                },
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                containerColor = containerColor,
                contentWindowInsets = contentWindowInsets,
            ) { padding -> content(padding, windowClass) }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                CoreNavigationRail(
                    items = items,
                    selectedIndex = selectedIndex,
                    onSelected = onSelected,
                    icon = icon,
                    header = railHeader,
                    modifier = Modifier
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                )
                CoreScaffold(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    topBar = {
                        CoreTopBar(
                            title = title,
                            subtitle = subtitle,
                            navigation = navigation,
                            actions = actions,
                            style = topBarStyle,
                        )
                    },
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    floatingActionButtonPosition = floatingActionButtonPosition,
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                ) { padding -> content(padding, windowClass) }
            }
        }
    }
}
