package com.msa.coreui.components

import com.msa.coreui.foundation.CoreUiWindowSizeClass

/** Validates the Material 3 compact bottom-navigation capacity used by adaptive navigation. */
internal fun validateBottomNavigationItemCount(itemCount: Int) {
    require(itemCount in 2..5) { "Bottom navigation requires between 2 and 5 items." }
}

/**
 * Uses a rail whenever the window is not compact or the destination count cannot fit a bottom bar.
 * This prevents width-dependent runtime failures when a responsive app exposes more than five
 * top-level destinations.
 */
internal fun shouldUseNavigationRail(
    windowClass: CoreUiWindowSizeClass,
    itemCount: Int,
): Boolean {
    require(itemCount >= 2) { "Adaptive navigation requires at least two items." }
    return windowClass != CoreUiWindowSizeClass.COMPACT || itemCount > 5
}
