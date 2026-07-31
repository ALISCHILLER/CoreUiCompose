package com.msa.coreui.components

import kotlin.math.abs

/** Shared validation for single-value and range slider contracts. */
internal fun validateSliderRange(range: ClosedFloatingPointRange<Float>) {
    require(range.start.isFinite() && range.endInclusive.isFinite()) {
        "Slider range values must be finite."
    }
    require(range.start <= range.endInclusive) { "Slider range start must not exceed its end." }
    require(abs(range.endInclusive - range.start) > 0f) { "Slider range must have a non-zero span." }
}
