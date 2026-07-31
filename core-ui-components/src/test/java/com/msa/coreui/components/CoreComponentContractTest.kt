package com.msa.coreui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CoreComponentContractTest {
    @Test
    fun button_content_rejects_blank_labels_and_descriptions() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreButtonContent(" ")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreButtonContent("Save", contentDescription = " ")
        }
    }

    @Test
    fun snackbar_content_rejects_blank_optional_actions() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreSnackbarContent(message = "Saved", actionLabel = " ")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreSnackbarContent(message = " ")
        }
    }

    @Test
    fun async_failure_is_value_based_and_validated() {
        val failure = CoreAsyncState.Failure("Could not load", "Try again")

        assertEquals(CoreAsyncState.Failure("Could not load", "Try again"), failure)
        assertThrows(IllegalArgumentException::class.java) {
            CoreAsyncState.Failure(" ")
        }
    }

    @Test
    fun tab_and_navigation_models_reject_blank_labels() {
        assertThrows(IllegalArgumentException::class.java) {
            CoreTabItem(" ")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreNavigationItem("Home", contentDescription = " ")
        }
    }

    @Test
    fun bottom_navigation_contract_rejects_width_dependent_crash_counts() {
        validateBottomNavigationItemCount(2)
        validateBottomNavigationItemCount(5)
        assertThrows(IllegalArgumentException::class.java) {
            validateBottomNavigationItemCount(1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            validateBottomNavigationItemCount(6)
        }
    }

    @Test
    fun section_header_contract_rejects_blank_copy() {
        assertThrows(IllegalArgumentException::class.java) {
            validateSectionHeaderCopy(" ", null)
        }
        assertThrows(IllegalArgumentException::class.java) {
            validateSectionHeaderCopy("Account", " ")
        }
    }

    @Test
    fun avatar_initials_use_first_and_last_words() {
        assertEquals("AS", avatarInitials("Ali Schiller"))
        assertEquals("M", avatarInitials("msa"))
        assertEquals("", avatarInitials("   "))
    }

    @Test
    fun avatar_initials_preserve_non_bmp_code_points() {
        assertEquals("😀S", avatarInitials("😀 Schiller"))
    }

    @Test
    fun character_count_treats_surrogate_pairs_as_one_character() {
        assertEquals(1, "😀".codePointLength())
        assertEquals(3, "A😀ب".codePointLength())
    }

    @Test
    fun text_length_guard_allows_recovery_from_an_external_over_limit_value() {
        assertEquals(true, shouldAcceptTextChange("123456789012", "12345678901", 10))
        assertEquals(true, shouldAcceptTextChange("12345678901", "1234567890", 10))
        assertEquals(false, shouldAcceptTextChange("1234567890", "12345678901", 10))
        assertEquals(true, shouldAcceptTextChange("anything", "anything longer", null))
    }

    @Test
    fun validation_issue_is_value_based_and_rejects_blank_copy() {
        val issue = CoreValidationIssue("Email", "Enter a valid address")

        assertEquals(CoreValidationIssue("Email", "Enter a valid address"), issue)
        assertThrows(IllegalArgumentException::class.java) {
            CoreValidationIssue(" ", "Required")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CoreValidationIssue("Email", " ")
        }
    }

}

class CoreNewComponentContractTest {
    @org.junit.Test
    fun segment_item_rejects_blank_accessibility_labels() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            CoreSegmentItem(label = "Valid", contentDescription = " ")
        }
    }

    @org.junit.Test
    fun segment_item_keeps_enabled_state() {
        val item = CoreSegmentItem(label = "Calendar", enabled = false)
        org.junit.Assert.assertEquals("Calendar", item.contentDescription)
        org.junit.Assert.assertFalse(item.enabled)
    }

    @org.junit.Test
    fun select_option_is_value_based_and_validated() {
        val option = CoreSelectOption(value = "stable", label = "Stable")
        org.junit.Assert.assertEquals(CoreSelectOption(value = "stable", label = "Stable"), option)
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            CoreSelectOption(value = "stable", label = " ")
        }
    }
}

class CorePaginationContractTest {
    @org.junit.Test
    fun page_window_stays_centered_and_clamped() {
        org.junit.Assert.assertEquals(1..5, calculateVisiblePages(1, 10, 5))
        org.junit.Assert.assertEquals(3..7, calculateVisiblePages(5, 10, 5))
        org.junit.Assert.assertEquals(6..10, calculateVisiblePages(10, 10, 5))
        org.junit.Assert.assertEquals(1..2, calculateVisiblePages(2, 2, 5))
    }

    @org.junit.Test
    fun page_window_rejects_invalid_contracts() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            calculateVisiblePages(0, 5, 3)
        }
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            calculateVisiblePages(1, 0, 3)
        }
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            calculateVisiblePages(1, 5, 0)
        }
    }
}

class CoreSliderContractTest {
    @org.junit.Test
    fun slider_range_contract_rejects_invalid_values() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            validateSliderRange(1f..1f)
        }
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            validateSliderRange(Float.NaN..1f)
        }
        validateSliderRange(0f..1f)
    }
}

class CoreAdaptiveNavigationContractTest {
    @org.junit.Test
    fun adaptive_navigation_uses_rail_for_overflowing_compact_destinations() {
        org.junit.Assert.assertFalse(
            shouldUseNavigationRail(
                com.msa.coreui.foundation.CoreUiWindowSizeClass.COMPACT,
                5,
            ),
        )
        org.junit.Assert.assertTrue(
            shouldUseNavigationRail(
                com.msa.coreui.foundation.CoreUiWindowSizeClass.COMPACT,
                6,
            ),
        )
        org.junit.Assert.assertTrue(
            shouldUseNavigationRail(
                com.msa.coreui.foundation.CoreUiWindowSizeClass.MEDIUM,
                2,
            ),
        )
    }
}

class CoreTextLengthPolicyContractTest {
    @org.junit.Test
    fun grapheme_policy_counts_combining_sequences_as_one_character() {
        val composed = "e\u0301"
        org.junit.Assert.assertEquals(2, composed.textLength(CoreTextLengthPolicy.CODE_POINTS))
        org.junit.Assert.assertEquals(1, composed.textLength(CoreTextLengthPolicy.GRAPHEME_CLUSTERS))
    }

    @org.junit.Test
    fun grapheme_limit_allows_recovery_from_invalid_external_values() {
        val current = "e\u0301a"
        val candidate = "e\u0301"
        org.junit.Assert.assertTrue(
            shouldAcceptTextChange(
                currentValue = current,
                candidateValue = candidate,
                maxLength = 1,
                lengthPolicy = CoreTextLengthPolicy.GRAPHEME_CLUSTERS,
            ),
        )
    }
}

class CoreMultiSelectContractTest {
    @org.junit.Test
    fun toggle_multi_selection_preserves_order_and_toggles_membership() {
        val selected = linkedSetOf("a", "b")
        org.junit.Assert.assertEquals(setOf("a", "b", "c"), toggleMultiSelection(selected, "c"))
        org.junit.Assert.assertEquals(setOf("a"), toggleMultiSelection(selected, "b"))
    }

    @org.junit.Test
    fun multi_select_contract_rejects_duplicates_and_sanitizes_external_state() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            validateMultiSelectContract(
                options = listOf(CoreSelectOption("a", "A"), CoreSelectOption("a", "Again")),
                maxSelection = null,
            )
        }
        validateMultiSelectContract(
            options = listOf(CoreSelectOption("a", "A"), CoreSelectOption("b", "B")),
            maxSelection = 1,
        )
        org.junit.Assert.assertEquals(
            setOf("a"),
            sanitizeMultiSelection(setOf("a", "legacy"), setOf("a", "b")),
        )
    }
}

class CoreTabsContractTest {
    @org.junit.Test
    fun tab_layout_scrolls_when_fixed_capacity_is_exceeded() {
        org.junit.Assert.assertFalse(shouldUseScrollableTabs(4, scrollable = false, maxFixedTabs = 4))
        org.junit.Assert.assertTrue(shouldUseScrollableTabs(5, scrollable = false, maxFixedTabs = 4))
        org.junit.Assert.assertTrue(shouldUseScrollableTabs(2, scrollable = true, maxFixedTabs = 4))
    }
}
