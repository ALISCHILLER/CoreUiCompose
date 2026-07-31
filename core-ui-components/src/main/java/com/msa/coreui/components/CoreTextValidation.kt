package com.msa.coreui.components

import java.text.BreakIterator
import java.util.Locale

/** Defines how text-field limits count user input. */
enum class CoreTextLengthPolicy {
    /** Counts Unicode code points. Fast and backward-compatible with previous Core UI releases. */
    CODE_POINTS,

    /** Counts user-perceived characters such as a base letter plus combining marks as one unit. */
    GRAPHEME_CLUSTERS,
}

/** Returns whether a controlled field should accept [candidateValue] under [maxLength]. */
internal fun shouldAcceptTextChange(
    currentValue: String,
    candidateValue: String,
    maxLength: Int?,
    lengthPolicy: CoreTextLengthPolicy = CoreTextLengthPolicy.CODE_POINTS,
    locale: Locale = Locale.getDefault(),
): Boolean {
    if (maxLength == null) return true
    val currentCount = currentValue.textLength(lengthPolicy, locale)
    val candidateCount = candidateValue.textLength(lengthPolicy, locale)
    return candidateCount <= maxLength || candidateCount < currentCount
}

/** Unicode-aware length used by public text-field constraints. */
internal fun String.textLength(
    policy: CoreTextLengthPolicy,
    locale: Locale = Locale.getDefault(),
): Int = when (policy) {
    CoreTextLengthPolicy.CODE_POINTS -> codePointLength()
    CoreTextLengthPolicy.GRAPHEME_CLUSTERS -> graphemeClusterLength(locale)
}

/** Unicode code-point count retained for compatibility with existing contract tests. */
internal fun String.codePointLength(): Int = codePointCount(0, length)

private fun String.graphemeClusterLength(locale: Locale): Int {
    if (isEmpty()) return 0
    val iterator = BreakIterator.getCharacterInstance(locale)
    iterator.setText(this)
    var count = 0
    var boundary = iterator.first()
    while (boundary != BreakIterator.DONE) {
        val next = iterator.next()
        if (next == BreakIterator.DONE) break
        count += 1
        boundary = next
    }
    return count
}
