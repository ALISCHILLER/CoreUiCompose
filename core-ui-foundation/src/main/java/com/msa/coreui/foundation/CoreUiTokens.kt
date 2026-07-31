package com.msa.coreui.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale shared by every Core UI component. */
@Immutable
data class CoreUiSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
) {
    init {
        requireNonNegative("spacing", listOf(none, xxs, xs, sm, md, lg, xl, xxl, xxxl))
        require(listOf(none, xxs, xs, sm, md, lg, xl, xxl, xxxl).zipWithNext().all { (a, b) -> a <= b }) {
            "Spacing tokens must be ordered from smallest to largest."
        }
    }
}

/** Corner-radius scale. Components may still accept their own Shape overrides. */
@Immutable
data class CoreUiRadii(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val full: Dp = 999.dp,
) {
    init {
        requireNonNegative("radii", listOf(none, xs, sm, md, lg, xl, full))
        require(listOf(none, xs, sm, md, lg, xl, full).zipWithNext().all { (a, b) -> a <= b }) {
            "Radius tokens must be ordered from smallest to largest."
        }
    }
}

/** Tonal and shadow elevation scale. */
@Immutable
data class CoreUiElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
) {
    init {
        requireNonNegative("elevation", listOf(level0, level1, level2, level3, level4, level5))
        require(listOf(level0, level1, level2, level3, level4, level5).zipWithNext().all { (a, b) -> a <= b }) {
            "Elevation tokens must be ordered from smallest to largest."
        }
    }
}

/**
 * Motion policy in milliseconds.
 *
 * Set [animationsEnabled] to false for product-level reduced-motion modes. Platform animation scale
 * is still respected by Compose animation clocks.
 */
@Immutable
data class CoreUiMotion(
    val animationsEnabled: Boolean = true,
    val instant: Int = 0,
    val fast: Int = 120,
    val normal: Int = 220,
    val slow: Int = 360,
) {
    init {
        require(listOf(instant, fast, normal, slow).all { it >= 0 }) { "Motion durations must not be negative." }
        require(instant <= fast && fast <= normal && normal <= slow) {
            "Motion durations must be ordered from fastest to slowest."
        }
    }
}

/** Minimum interactive sizes used by accessible components. */
@Immutable
data class CoreUiSizing(
    val touchTarget: Dp = 48.dp,
    val buttonHeight: Dp = 48.dp,
    val compactButtonHeight: Dp = 40.dp,
    val largeButtonHeight: Dp = 56.dp,
    val fieldMinHeight: Dp = 56.dp,
    val listItemMinHeight: Dp = 56.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val badgeMinHeight: Dp = 24.dp,
    val topBarMinHeight: Dp = 64.dp,
    val contentMaxWidth: Dp = 840.dp,
    val dialogMaxWidth: Dp = 560.dp,
    val dialogMaxHeight: Dp = 760.dp,
    val sheetMaxWidth: Dp = 720.dp,
    val navigationBarHeight: Dp = 80.dp,
    val navigationRailWidth: Dp = 80.dp,
    val avatarSmall: Dp = 32.dp,
    val avatarMedium: Dp = 48.dp,
    val avatarLarge: Dp = 72.dp,
    val skeletonLineHeight: Dp = 14.dp,
    val segmentedControlMinHeight: Dp = 48.dp,
    val stepIndicatorSize: Dp = 28.dp,
    val selectMenuMaxHeight: Dp = 320.dp,
    val adaptiveGridMinCellWidth: Dp = 280.dp,
    val actionStackBreakpoint: Dp = 420.dp,
) {
    init {
        require(
            listOf(
                touchTarget,
                buttonHeight,
                compactButtonHeight,
                largeButtonHeight,
                fieldMinHeight,
                listItemMinHeight,
                iconSmall,
                iconMedium,
                iconLarge,
                badgeMinHeight,
                topBarMinHeight,
                contentMaxWidth,
                dialogMaxWidth,
                dialogMaxHeight,
                sheetMaxWidth,
                navigationBarHeight,
                navigationRailWidth,
                avatarSmall,
                avatarMedium,
                avatarLarge,
                skeletonLineHeight,
                segmentedControlMinHeight,
                stepIndicatorSize,
                selectMenuMaxHeight,
                adaptiveGridMinCellWidth,
                actionStackBreakpoint,
            ).all { it > 0.dp },
        ) {
            "Sizing tokens must be greater than zero."
        }
        require(touchTarget >= 48.dp) { "touchTarget must be at least 48.dp for accessibility." }
        require(largeButtonHeight >= buttonHeight) { "largeButtonHeight must not be smaller than buttonHeight." }
        require(fieldMinHeight >= touchTarget) { "fieldMinHeight must not be smaller than touchTarget." }
        require(listItemMinHeight >= touchTarget) { "listItemMinHeight must not be smaller than touchTarget." }
        require(iconSmall <= iconMedium && iconMedium <= iconLarge) { "Icon sizes must be ordered." }
        require(badgeMinHeight <= touchTarget) { "badgeMinHeight must not exceed touchTarget." }
        require(topBarMinHeight >= touchTarget) { "topBarMinHeight must not be smaller than touchTarget." }
        require(dialogMaxWidth <= contentMaxWidth) { "Dialog maximum width must not exceed content maximum width." }
        require(sheetMaxWidth <= contentMaxWidth) { "Sheet maximum width must not exceed content maximum width." }
        require(avatarSmall <= avatarMedium && avatarMedium <= avatarLarge) { "Avatar sizes must be ordered." }
        require(navigationBarHeight >= touchTarget) { "navigationBarHeight must not be smaller than touchTarget." }
        require(navigationRailWidth >= touchTarget) { "navigationRailWidth must not be smaller than touchTarget." }
        require(segmentedControlMinHeight >= touchTarget) {
            "segmentedControlMinHeight must not be smaller than touchTarget."
        }
    }
}

/** Interaction-state opacity scale for custom components. */
@Immutable
data class CoreUiStateLayers(
    val hover: Float = 0.08f,
    val focus: Float = 0.12f,
    val pressed: Float = 0.12f,
    val dragged: Float = 0.16f,
) {
    init {
        require(listOf(hover, focus, pressed, dragged).all { it in 0f..1f }) {
            "State-layer alpha tokens must be between 0 and 1."
        }
    }
}

/** Opacity scale used for disabled, muted and outlined states. */
@Immutable
data class CoreUiAlpha(
    val disabledContainer: Float = 0.12f,
    val disabledContent: Float = 0.38f,
    val mutedContent: Float = 0.48f,
    val secondaryContent: Float = 0.72f,
    val outline: Float = 0.45f,
) {
    init {
        require(listOf(disabledContainer, disabledContent, mutedContent, secondaryContent, outline).all { it in 0f..1f }) {
            "Alpha tokens must be between 0 and 1."
        }
        require(disabledContainer <= disabledContent && disabledContent <= mutedContent && mutedContent <= secondaryContent) {
            "Alpha emphasis must be ordered from disabled container to secondary content."
        }
    }
}

/** Border and indicator thickness scale. */
@Immutable
data class CoreUiStroke(
    val hairline: Dp = 1.dp,
    val emphasis: Dp = 1.5.dp,
    val strong: Dp = 2.dp,
) {
    init {
        require(listOf(hairline, emphasis, strong).all { it > 0.dp }) {
            "Stroke tokens must be greater than zero."
        }
        require(hairline <= emphasis && emphasis <= strong) {
            "Stroke tokens must be ordered from thinnest to strongest."
        }
    }
}

/** Width classes used by adaptive Core UI layouts. */
@Immutable
data class CoreUiBreakpoints(
    val compactMaxWidth: Dp = 599.dp,
    val mediumMaxWidth: Dp = 839.dp,
) {
    init {
        require(compactMaxWidth > 0.dp) { "compactMaxWidth must be greater than zero." }
        require(mediumMaxWidth > compactMaxWidth) { "mediumMaxWidth must be greater than compactMaxWidth." }
    }
}

enum class CoreUiWindowSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED,
}

fun CoreUiBreakpoints.classify(width: Dp): CoreUiWindowSizeClass {
    require(width >= 0.dp) { "width must not be negative." }
    return when {
        width <= compactMaxWidth -> CoreUiWindowSizeClass.COMPACT
        width <= mediumMaxWidth -> CoreUiWindowSizeClass.MEDIUM
        else -> CoreUiWindowSizeClass.EXPANDED
    }
}

private fun requireNonNegative(name: String, values: List<Dp>) {
    require(values.all { it >= 0.dp }) { "$name tokens must not be negative." }
}
