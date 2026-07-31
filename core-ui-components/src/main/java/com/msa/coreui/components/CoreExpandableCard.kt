package com.msa.coreui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.msa.coreui.foundation.CoreUiTheme

/** Controlled disclosure card for FAQs, filters, settings, and progressive-detail interfaces. */
@Composable
fun CoreExpandableCard(
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    require(title.isNotBlank()) { "title must not be blank." }
    require(supportingText == null || supportingText.isNotBlank()) {
        "supportingText must be null or non-blank."
    }
    val motion = CoreUiTheme.motion
    val strings = CoreUiTheme.strings
    val animatedModifier = if (motion.animationsEnabled) {
        modifier.animateContentSize(animationSpec = tween(motion.normal))
    } else {
        modifier
    }
    val rotation = if (motion.animationsEnabled) {
        animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(motion.fast),
            label = "Expandable card chevron",
        ).value
    } else if (expanded) 180f else 0f

    CoreCard(modifier = animatedModifier) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = enabled,
                        role = Role.Button,
                        onClick = { onExpandedChange(!expanded) },
                    )
                    .semantics {
                        role = Role.Button
                        stateDescription = if (expanded) {
                            strings.expanded
                        } else {
                            strings.collapsed
                        }
                        if (enabled) {
                            onClick(
                                label = if (expanded) {
                                    strings.collapse
                                } else {
                                    strings.expand
                                },
                                action = {
                                    onExpandedChange(!expanded)
                                    true
                                },
                            )
                        } else {
                            disabled()
                        }
                    }
                    .padding(CoreUiTheme.spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading?.invoke()
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (supportingText != null) {
                        Text(
                            text = supportingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                CoreIcon(
                    imageVector = CoreIcons.ChevronDown,
                    contentDescription = null,
                    decorative = true,
                    modifier = Modifier
                        .size(CoreUiTheme.sizing.iconMedium)
                        .rotate(rotation),
                )
            }
            if (motion.animationsEnabled) {
                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = CoreUiTheme.spacing.lg,
                                end = CoreUiTheme.spacing.lg,
                                bottom = CoreUiTheme.spacing.lg,
                            ),
                        content = content,
                    )
                }
            } else if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = CoreUiTheme.spacing.lg,
                            end = CoreUiTheme.spacing.lg,
                            bottom = CoreUiTheme.spacing.lg,
                        ),
                    content = content,
                )
            }
        }
    }
}
