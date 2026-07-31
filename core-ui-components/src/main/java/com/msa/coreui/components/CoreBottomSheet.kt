package com.msa.coreui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.msa.coreui.foundation.CoreUiTheme

/** Controlled modal sheet with adaptive content width, IME handling, and optional close action. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    skipPartiallyExpanded: Boolean = true,
    showDragHandle: Boolean = true,
    showCloseAction: Boolean = title != null,
    dismissEnabled: Boolean = true,
    busy: Boolean = false,
    busyDescription: String = CoreUiTheme.strings.loading,
    closeContentDescription: String = CoreUiTheme.strings.close,
    maxContentWidth: Dp = CoreUiTheme.sizing.sheetMaxWidth,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(
        start = CoreUiTheme.spacing.lg,
        top = CoreUiTheme.spacing.sm,
        end = CoreUiTheme.spacing.lg,
        bottom = CoreUiTheme.spacing.xl,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    require(title == null || title.isNotBlank()) { "title must be null or non-blank." }
    require(closeContentDescription.isNotBlank()) { "closeContentDescription must not be blank." }
    require(busyDescription.isNotBlank()) { "busyDescription must not be blank." }
    require(maxContentWidth > 0.dp) { "maxContentWidth must be greater than zero." }
    if (!visible) return

    val effectiveDismissEnabled = dismissEnabled && !busy
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = { target -> effectiveDismissEnabled || target != SheetValue.Hidden },
    )
    ModalBottomSheet(
        onDismissRequest = { if (effectiveDismissEnabled) onDismissRequest() },
        modifier = modifier,
        sheetState = sheetState,
        containerColor = containerColor,
        dragHandle = if (showDragHandle && effectiveDismissEnabled) ({ BottomSheetDefaults.DragHandle() }) else null,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxWidth()
                    .padding(contentPadding),
            ) {
                if (title != null || showCloseAction || busy) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = CoreUiTheme.spacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f).semantics { heading() },
                            )
                        } else {
                            Box(Modifier.weight(1f))
                        }
                        if (busy) {
                            CoreCircularProgress(
                                progress = null,
                                contentDescription = busyDescription,
                                modifier = Modifier.size(CoreUiTheme.sizing.iconMedium),
                            )
                        } else if (showCloseAction) {
                            CoreIconButton(
                                onClick = onDismissRequest,
                                contentDescription = closeContentDescription,
                                enabled = effectiveDismissEnabled,
                            ) {
                                CoreIcon(
                                    imageVector = CoreIcons.Close,
                                    contentDescription = null,
                                    decorative = true,
                                )
                            }
                        }
                    }
                }
                content()
            }
        }
    }
}
