package com.msa.coreui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.msa.coreui.foundation.CoreUiTheme

@Immutable
data class CoreSegmentItem(
    val label: String,
    val contentDescription: String = label,
    val enabled: Boolean = true,
) {
    init {
        require(label.isNotBlank()) { "label must not be blank." }
        require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    }
}

/** Connected single-choice segmented control with caller-owned selection. */
@Composable
fun CoreSegmentedControl(
    items: List<CoreSegmentItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    equalWidth: Boolean = true,
    leadingIcon: (@Composable (item: CoreSegmentItem, selected: Boolean) -> Unit)? = null,
) {
    require(items.size >= 2) { "A segmented control requires at least two items." }
    require(selectedIndex in items.indices) { "selectedIndex must point to an existing item." }
    require(items[selectedIndex].enabled) { "The selected segment must be enabled." }

    SegmentContainer(modifier = modifier.selectableGroup()) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            SegmentItem(
                item = item,
                selected = selected,
                modifier = if (equalWidth) Modifier.weight(1f) else Modifier,
                onClick = { onSelected(index) },
                leadingIcon = leadingIcon,
            )
            if (index != items.lastIndex) SegmentDivider()
        }
    }
}

/** Connected multi-choice segmented control. */
@Composable
fun CoreMultiSegmentedControl(
    items: List<CoreSegmentItem>,
    selectedIndices: Set<Int>,
    onSelectionChange: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier,
    equalWidth: Boolean = true,
    leadingIcon: (@Composable (item: CoreSegmentItem, selected: Boolean) -> Unit)? = null,
) {
    require(items.size >= 2) { "A segmented control requires at least two items." }
    require(selectedIndices.all(items.indices::contains)) { "selectedIndices contains an invalid index." }
    require(selectedIndices.all { index -> items[index].enabled }) {
        "selectedIndices must not contain disabled items."
    }

    SegmentContainer(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val selected = index in selectedIndices
            val itemModifier = (if (equalWidth) Modifier.weight(1f) else Modifier)
                .toggleable(
                    value = selected,
                    enabled = item.enabled,
                    role = Role.Checkbox,
                    onValueChange = { checked ->
                        onSelectionChange(
                            if (checked) selectedIndices + index else selectedIndices - index,
                        )
                    },
                )
            SegmentVisual(
                item = item,
                selected = selected,
                modifier = itemModifier,
                leadingIcon = leadingIcon,
            )
            if (index != items.lastIndex) SegmentDivider()
        }
    }
}

@Composable
private fun SegmentContainer(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = CoreUiTheme.sizing.segmentedControlMinHeight)
            .clip(shape)
            .border(
                width = CoreUiTheme.stroke.hairline,
                color = MaterialTheme.colorScheme.outline,
                shape = shape,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.SegmentItem(
    item: CoreSegmentItem,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    leadingIcon: (@Composable (item: CoreSegmentItem, selected: Boolean) -> Unit)?,
) {
    SegmentVisual(
        item = item,
        selected = selected,
        modifier = modifier.selectable(
            selected = selected,
            enabled = item.enabled,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        leadingIcon = leadingIcon,
    )
}

@Composable
private fun SegmentVisual(
    item: CoreSegmentItem,
    selected: Boolean,
    modifier: Modifier,
    leadingIcon: (@Composable (item: CoreSegmentItem, selected: Boolean) -> Unit)?,
) {
    val container = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val alpha = if (item.enabled) 1f else CoreUiTheme.alpha.disabledContent

    Row(
        modifier = modifier
            .heightIn(min = CoreUiTheme.sizing.segmentedControlMinHeight)
            .background(container)
            .semantics {
                if (item.contentDescription != item.label) {
                    contentDescription = item.contentDescription
                }
                if (!item.enabled) disabled()
            }
            .padding(horizontal = CoreUiTheme.spacing.md, vertical = CoreUiTheme.spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Box(Modifier.clearAndSetSemantics { }) {
                leadingIcon(item, selected)
            }
            Spacer(Modifier.width(CoreUiTheme.spacing.xs))
        }
        Text(
            text = item.label,
            color = contentColor.copy(alpha = alpha),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RowScope.SegmentDivider() {
    Box(
        modifier = Modifier
            .width(CoreUiTheme.stroke.hairline)
            .heightIn(min = CoreUiTheme.sizing.touchTarget)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = CoreUiTheme.alpha.outline)),
    )
}
