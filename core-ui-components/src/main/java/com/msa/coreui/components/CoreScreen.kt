package com.msa.coreui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow

/** App-bar hierarchy used by [CoreScreen] and [CoreTopBar]. */
enum class CoreTopBarStyle { SMALL, CENTERED, LARGE }

/** Predictable screen shell with reusable app bar, bottom surface, feedback, and edge-to-edge insets. */
@Composable
fun CoreScreen(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigation: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBarStyle: CoreTopBarStyle = CoreTopBarStyle.SMALL,
    content: @Composable (PaddingValues) -> Unit,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(subtitle == null || subtitle.isNotBlank()) { "subtitle must be null or non-blank." }
    CoreScaffold(
        modifier = modifier,
        topBar = {
            CoreTopBar(
                title = title,
                subtitle = subtitle,
                navigation = navigation,
                actions = actions,
                style = topBarStyle,
            )
        },
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
        content = content,
    )
}

/** App-bar primitive that keeps Material scroll behavior out of the public API. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigation: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    style: CoreTopBarStyle = CoreTopBarStyle.SMALL,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(subtitle == null || subtitle.isNotBlank()) { "subtitle must be null or non-blank." }
    val titleContent: @Composable () -> Unit = {
        Column {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = when (style) {
                    CoreTopBarStyle.LARGE -> MaterialTheme.typography.headlineMedium
                    else -> MaterialTheme.typography.titleLarge
                },
                maxLines = if (style == CoreTopBarStyle.LARGE) 2 else 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = com.msa.coreui.foundation.CoreUiTheme.alpha.secondaryContent),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        scrolledContainerColor = containerColor,
        navigationIconContentColor = contentColor,
        titleContentColor = contentColor,
        actionIconContentColor = contentColor,
    )
    when (style) {
        CoreTopBarStyle.SMALL -> TopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigation ?: {},
            actions = actions,
            colors = colors,
        )
        CoreTopBarStyle.CENTERED -> CenterAlignedTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigation ?: {},
            actions = actions,
            colors = colors,
        )
        CoreTopBarStyle.LARGE -> LargeTopAppBar(
            title = titleContent,
            modifier = modifier,
            navigationIcon = navigation ?: {},
            actions = actions,
            colors = colors,
        )
    }
}
