package com.msa.coreui.components

/** Calculates a stable one-based page window around [currentPage]. */
internal fun calculateVisiblePages(
    currentPage: Int,
    pageCount: Int,
    maxVisiblePages: Int,
): IntRange {
    require(pageCount > 0) { "pageCount must be greater than zero." }
    require(currentPage in 1..pageCount) { "currentPage must be inside 1..pageCount." }
    require(maxVisiblePages >= 1) { "maxVisiblePages must be at least one." }
    val visibleCount = minOf(maxVisiblePages, pageCount)
    val half = visibleCount / 2
    var start = (currentPage - half).coerceAtLeast(1)
    var end = start + visibleCount - 1
    if (end > pageCount) {
        end = pageCount
        start = (end - visibleCount + 1).coerceAtLeast(1)
    }
    return start..end
}
