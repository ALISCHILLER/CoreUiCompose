package com.msa.coreui.foundation

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CoreUiTokensTest {
    @Test
    fun breakpoints_classify_boundary_widths() {
        val breakpoints = CoreUiBreakpoints()

        assertEquals(CoreUiWindowSizeClass.COMPACT, breakpoints.classify(599.dp))
        assertEquals(CoreUiWindowSizeClass.MEDIUM, breakpoints.classify(600.dp))
        assertEquals(CoreUiWindowSizeClass.MEDIUM, breakpoints.classify(839.dp))
        assertEquals(CoreUiWindowSizeClass.EXPANDED, breakpoints.classify(840.dp))
    }

    @Test
    fun invalid_token_scales_are_rejected() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiSpacing(sm = 16.dp, md = 12.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiRadii(sm = 16.dp, md = 12.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiMotion(fast = 300, normal = 200)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiBreakpoints(compactMaxWidth = 800.dp, mediumMaxWidth = 700.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiAlpha(disabledContent = 1.1f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiStroke(hairline = 3.dp, emphasis = 2.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiStrings(loading = " ")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiStrings(search = " ")
        }
    }

    @Test
    fun accessibility_sizing_rejects_undersized_touch_targets() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiSizing(touchTarget = 40.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiSizing(fieldMinHeight = 44.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiSizing(badgeMinHeight = 56.dp)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiSizing(topBarMinHeight = 40.dp)
        }
    }

    @Test
    fun localized_strings_cover_empty_selection_and_length_overflow_states() {
        assertEquals("Not selected", CoreUiStrings.English.notSelected)
        assertEquals("No options available", CoreUiStrings.English.noOptions)
        assertEquals("2 characters over the limit", CoreUiStrings.English.charactersOverLimit(2))
        assertEquals("۲ نویسه بیشتر از حد مجاز", CoreUiStrings.Persian.charactersOverLimit(2))
        assertEquals("۱۲۳", CoreUiStrings.Persian.formatNumber(123))
        assertEquals("صفحه ۲ از ۱۰", CoreUiStrings.Persian.pageOf(2, 10))
    }

    @Test
    fun breakpoints_reject_negative_widths() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreUiBreakpoints().classify((-1).dp)
        }
    }
}

class CoreUiStringFormattingTest {
    @org.junit.Test
    fun decimal_and_percentage_formatting_are_locale_contracts() {
        org.junit.Assert.assertEquals("12.5", CoreUiStrings.English.formatDecimal(12.5f))
        org.junit.Assert.assertEquals("50%", CoreUiStrings.English.formatPercentage(0.5f))
        org.junit.Assert.assertEquals("۱۲.۵", CoreUiStrings.Persian.formatDecimal(12.5f))
        org.junit.Assert.assertEquals("۵۰٪", CoreUiStrings.Persian.formatPercentage(0.5f))
    }
}
