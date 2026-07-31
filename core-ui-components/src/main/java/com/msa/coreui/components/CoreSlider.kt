package com.msa.coreui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.msa.coreui.foundation.CoreUiTheme

/** Controlled Material 3 slider with a visible value label and explicit accessibility semantics. */
@Composable
fun CoreSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    showValue: Boolean = true,
    valueFormatter: ((Float) -> String)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    contentDescription: String = label,
) {
    validateSliderRange(valueRange)
    require(value.isFinite()) { "value must be finite." }
    require(value in valueRange) { "value must be inside valueRange." }
    require(steps >= 0) { "steps must not be negative." }
    require(label.isNotBlank()) { "label must not be blank." }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }

    val formatter = valueFormatter ?: CoreUiTheme.strings.formatDecimal
    val formattedValue = formatter(value).also {
        require(it.isNotBlank()) { "valueFormatter must return non-blank text." }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            if (showValue) {
                Text(
                    text = formattedValue,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    this.contentDescription = contentDescription
                    stateDescription = formattedValue
                },
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
        )
    }
}

/** Controlled range slider with inclusive start/end labels and validation. */
@Composable
fun CoreRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    showValues: Boolean = true,
    valueFormatter: ((Float) -> String)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    contentDescription: String = label,
) {
    validateSliderRange(valueRange)
    validateSliderRange(value)
    require(value.start >= valueRange.start && value.endInclusive <= valueRange.endInclusive) {
        "value must be inside valueRange."
    }
    require(steps >= 0) { "steps must not be negative." }
    require(label.isNotBlank()) { "label must not be blank." }
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }

    val formatter = valueFormatter ?: CoreUiTheme.strings.formatDecimal
    val startLabel = formatter(value.start).also {
        require(it.isNotBlank()) { "valueFormatter must return non-blank text." }
    }
    val endLabel = formatter(value.endInclusive).also {
        require(it.isNotBlank()) { "valueFormatter must return non-blank text." }
    }
    val rangeDescription = "$startLabel – $endLabel"

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CoreUiTheme.spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            if (showValues) {
                Text(
                    text = rangeDescription,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        RangeSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    this.contentDescription = contentDescription
                    stateDescription = rangeDescription
                },
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
        )
    }
}

