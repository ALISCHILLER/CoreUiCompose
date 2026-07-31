package com.msa.coreui.components

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.msa.coreui.foundation.CoreUiTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CoreFormAndPaginationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun stale_select_value_is_rendered_as_validation_instead_of_crashing() {
        composeRule.setContent {
            CoreUiTheme {
                CoreSelectField(
                    options = emptyList<CoreSelectOption<String>>(),
                    selectedValue = "legacy",
                    selectedValueLabel = "Legacy option",
                    onValueSelected = {},
                    label = "Channel",
                )
            }
        }

        composeRule.onNodeWithText("Legacy option").assertExists()
        composeRule.onNodeWithText("The selected value is no longer available").assertExists()
    }

    @Test
    fun slider_exposes_formatted_accessibility_value() {
        composeRule.setContent {
            CoreUiTheme {
                CoreSlider(
                    value = 50f,
                    onValueChange = {},
                    label = "Volume",
                    valueRange = 0f..100f,
                    valueFormatter = { "${it.toInt()} percent" },
                )
            }
        }

        composeRule.onAllNodesWithContentDescription("Volume").assertCountEquals(1)
        composeRule.onNodeWithContentDescription("Volume").assertExists()
        composeRule.onNodeWithText("50 percent").assertExists()
    }

    @Test
    fun pagination_reports_one_based_page_selection() {
        var selectedPage = 1
        composeRule.setContent {
            CoreUiTheme {
                CorePagination(
                    currentPage = 1,
                    pageCount = 8,
                    onPageSelected = { selectedPage = it },
                )
            }
        }

        composeRule.onNodeWithText("2").performClick()
        composeRule.runOnIdle { assertEquals(2, selectedPage) }
    }

    @Test
    fun stale_select_does_not_render_raw_model_to_string_when_label_is_missing() {
        composeRule.setContent {
            CoreUiTheme {
                CoreSelectField(
                    options = emptyList<CoreSelectOption<String>>(),
                    selectedValue = "internal-id-42",
                    onValueSelected = {},
                    label = "Channel",
                )
            }
        }

        composeRule.onNodeWithText("internal-id-42").assertDoesNotExist()
        composeRule.onNodeWithText("The selected value is no longer available").assertExists()
    }

    @Test
    fun multi_select_recovers_from_stale_and_over_limit_external_state() {
        var latestSelection: Set<String> = emptySet()
        composeRule.setContent {
            var selected by remember { mutableStateOf(setOf("a", "legacy")) }
            CoreUiTheme {
                CoreMultiSelectField(
                    options = listOf(
                        CoreSelectOption("a", "A"),
                        CoreSelectOption("b", "B"),
                    ),
                    selectedValues = selected,
                    onSelectionChange = { updated ->
                        selected = updated
                        latestSelection = updated
                    },
                    label = "Modules",
                    maxSelection = 1,
                )
            }
        }

        composeRule.onNodeWithText("The selected value is no longer available").assertExists()
        composeRule.onNodeWithContentDescription("Modules").performClick()
        composeRule.onNodeWithText("A").performClick()
        composeRule.runOnIdle { assertEquals(emptySet<String>(), latestSelection) }

        composeRule.onNodeWithText("B").performClick()
        composeRule.runOnIdle { assertEquals(setOf("b"), latestSelection) }
    }

}
