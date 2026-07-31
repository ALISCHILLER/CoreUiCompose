package com.msa.coreui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.msa.coreui.foundation.CoreUiTheme

/** Accessible floating action button with optional extended label. */
@Composable
fun CoreFloatingActionButton(
    onClick: () -> Unit,
    contentDescription: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    require(label == null || label.isNotBlank()) { "label must be null or non-blank." }
    val semanticsModifier = if (label == null || contentDescription != label) {
        modifier.semantics { this.contentDescription = contentDescription }
    } else {
        modifier
    }

    if (label == null) {
        FloatingActionButton(
            onClick = onClick,
            modifier = semanticsModifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            content = { androidx.compose.foundation.layout.Box(Modifier.clearAndSetSemantics { }) { icon() } },
        )
    } else {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = semanticsModifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.layout.Box(Modifier.clearAndSetSemantics { }) { icon() }
                Spacer(Modifier.width(CoreUiTheme.spacing.sm))
                Text(label)
            }
        }
    }
}
