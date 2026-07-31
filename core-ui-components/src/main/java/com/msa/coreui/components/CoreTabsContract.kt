package com.msa.coreui.components

internal fun shouldUseScrollableTabs(
    itemCount: Int,
    scrollable: Boolean,
    maxFixedTabs: Int,
): Boolean {
    require(itemCount >= 2) { "Tabs require at least two items." }
    require(maxFixedTabs >= 2) { "maxFixedTabs must be at least two." }
    return scrollable || itemCount > maxFixedTabs
}
