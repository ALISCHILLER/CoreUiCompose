package com.msa.coreui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import com.msa.coreui.foundation.CoreUiTheme

/** Linear progress with a nullable progress value (`null` means indeterminate). */
@Composable
fun CoreLinearProgress(
    progress: Float?,
    modifier: Modifier = Modifier,
    contentDescription: String = CoreUiTheme.strings.loading,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    announcePercentage: Boolean = true,
) {
    require(progress == null || (progress.isFinite() && progress in 0f..1f)) {
        "progress must be null or between 0 and 1."
    }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    val normalized = progress
    val percentageDescription = if (normalized != null && announcePercentage) {
        CoreUiTheme.strings.formatPercentage(normalized)
    } else {
        null
    }
    val semanticsModifier = modifier
        .fillMaxWidth()
        .semantics {
            this.contentDescription = contentDescription
            progressBarRangeInfo = normalized?.let {
                ProgressBarRangeInfo(it, 0f..1f)
            } ?: ProgressBarRangeInfo.Indeterminate
            percentageDescription?.let { stateDescription = it }
        }
    if (normalized == null) {
        LinearProgressIndicator(
            modifier = semanticsModifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        LinearProgressIndicator(
            progress = { normalized },
            modifier = semanticsModifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

/** Circular progress with a nullable progress value (`null` means indeterminate). */
@Composable
fun CoreCircularProgress(
    progress: Float?,
    modifier: Modifier = Modifier,
    contentDescription: String = CoreUiTheme.strings.loading,
    color: Color = MaterialTheme.colorScheme.primary,
    announcePercentage: Boolean = true,
) {
    require(progress == null || (progress.isFinite() && progress in 0f..1f)) {
        "progress must be null or between 0 and 1."
    }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    val normalized = progress
    val percentageDescription = if (normalized != null && announcePercentage) {
        CoreUiTheme.strings.formatPercentage(normalized)
    } else {
        null
    }
    val semanticsModifier = modifier.semantics {
        this.contentDescription = contentDescription
        progressBarRangeInfo = normalized?.let {
            ProgressBarRangeInfo(it, 0f..1f)
        } ?: ProgressBarRangeInfo.Indeterminate
        percentageDescription?.let { stateDescription = it }
    }
    if (normalized == null) {
        CircularProgressIndicator(
            modifier = semanticsModifier,
            color = color,
            strokeWidth = CoreUiTheme.stroke.strong,
        )
    } else {
        CircularProgressIndicator(
            progress = { normalized },
            modifier = semanticsModifier,
            color = color,
            strokeWidth = CoreUiTheme.stroke.strong,
        )
    }
}
