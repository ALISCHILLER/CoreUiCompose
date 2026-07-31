package com.msa.coreui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.msa.coreui.foundation.CoreUiTheme

@Immutable
data class CoreStep(
    val title: String,
    val supportingText: String? = null,
) {
    init {
        require(title.isNotBlank()) { "title must not be blank." }
        require(supportingText == null || supportingText.isNotBlank()) {
            "supportingText must be null or non-blank."
        }
    }
}

enum class CoreStepState { COMPLETE, CURRENT, UPCOMING, ERROR }

/** Vertical process stepper with explicit state mapping and screen-reader progress. */
@Composable
fun CoreVerticalStepper(
    steps: List<CoreStep>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    errorIndices: Set<Int> = emptySet(),
) {
    require(steps.isNotEmpty()) { "steps must not be empty." }
    require(currentIndex in steps.indices) { "currentIndex must point to an existing step." }
    require(errorIndices.all(steps.indices::contains)) { "errorIndices contains an invalid index." }
    val stepDescription = CoreUiTheme.strings.stepOf(currentIndex + 1, steps.size)

    Column(
        modifier = modifier.semantics {
            stateDescription = stepDescription
            collectionInfo = CollectionInfo(rowCount = steps.size, columnCount = 1)
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = (currentIndex + 1).toFloat(),
                range = 1f..steps.size.toFloat(),
                steps = (steps.size - 2).coerceAtLeast(0),
            )
        },
    ) {
        steps.forEachIndexed { index, step ->
            val state = when {
                index in errorIndices -> CoreStepState.ERROR
                index < currentIndex -> CoreStepState.COMPLETE
                index == currentIndex -> CoreStepState.CURRENT
                else -> CoreStepState.UPCOMING
            }
            StepRow(
                step = step,
                index = index,
                total = steps.size,
                state = state,
                showConnector = index != steps.lastIndex,
            )
        }
    }
}

/** Compact progress indicator for wizard headers. */
@Composable
fun CoreStepProgress(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    require(totalSteps > 0) { "totalSteps must be greater than zero." }
    require(currentStep in 1..totalSteps) { "currentStep must be between 1 and totalSteps." }
    val stepDescription = CoreUiTheme.strings.stepOf(currentStep, totalSteps)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                stateDescription = stepDescription
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = currentStep.toFloat(),
                    range = 1f..totalSteps.toFloat(),
                    steps = (totalSteps - 2).coerceAtLeast(0),
                )
            },
        horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
    ) {
        repeat(totalSteps) { index ->
            val active = index < currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(CoreUiTheme.stroke.strong * 2f)
                    .background(
                        color = if (active) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                    ),
            )
        }
    }
}

@Composable
private fun StepRow(
    step: CoreStep,
    index: Int,
    total: Int,
    state: CoreStepState,
    showConnector: Boolean,
) {
    val strings = CoreUiTheme.strings
    val stateLabel = when (state) {
        CoreStepState.COMPLETE -> strings.completed
        CoreStepState.CURRENT -> strings.current
        CoreStepState.UPCOMING -> strings.upcoming
        CoreStepState.ERROR -> strings.error
    }
    val stepDescription = listOf(strings.stepOf(index + 1, total), stateLabel).joinToString(", ")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                collectionItemInfo = CollectionItemInfo(
                    rowIndex = index,
                    rowSpan = 1,
                    columnIndex = 0,
                    columnSpan = 1,
                )
                stateDescription = stepDescription
            },
        horizontalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StepIndicator(index = index, state = state)
            if (showConnector) {
                Spacer(
                    modifier = Modifier
                        .width(CoreUiTheme.stroke.emphasis)
                        .height(CoreUiTheme.spacing.xxl)
                        .background(
                            if (state == CoreStepState.COMPLETE) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            },
                        ),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showConnector) CoreUiTheme.spacing.lg else CoreUiTheme.spacing.none),
            verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (state == CoreStepState.CURRENT) FontWeight.SemiBold else FontWeight.Medium,
                color = when (state) {
                    CoreStepState.ERROR -> MaterialTheme.colorScheme.error
                    CoreStepState.UPCOMING -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
            if (step.supportingText != null) {
                Text(
                    text = step.supportingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = CoreUiTheme.strings.stepOf(index + 1, total),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StepIndicator(index: Int, state: CoreStepState) {
    val background: Color
    val content: Color
    when (state) {
        CoreStepState.COMPLETE, CoreStepState.CURRENT -> {
            background = MaterialTheme.colorScheme.primary
            content = MaterialTheme.colorScheme.onPrimary
        }
        CoreStepState.ERROR -> {
            background = MaterialTheme.colorScheme.error
            content = MaterialTheme.colorScheme.onError
        }
        CoreStepState.UPCOMING -> {
            background = MaterialTheme.colorScheme.surface
            content = MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    Box(
        modifier = Modifier
            .size(CoreUiTheme.sizing.stepIndicatorSize)
            .background(background, CircleShape)
            .border(CoreUiTheme.stroke.hairline, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (state == CoreStepState.COMPLETE) {
            CoreIcon(
                imageVector = CoreIcons.Check,
                contentDescription = null,
                decorative = true,
                modifier = Modifier.size(CoreUiTheme.sizing.iconSmall),
                tint = content,
            )
        } else {
            Text(
                text = CoreUiTheme.strings.formatNumber(index + 1),
                color = content,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
