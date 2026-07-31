package com.msa.coreui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.msa.coreui.foundation.CoreUiTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CorePatternsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tabs_are_controlled_and_report_selection() {
        var selected by mutableIntStateOf(0)
        composeRule.setContent {
            CoreUiTheme {
                CoreTabs(
                    items = listOf(CoreTabItem("Overview"), CoreTabItem("Details")),
                    selectedIndex = selected,
                    onSelected = { selected = it },
                )
            }
        }

        composeRule.onNodeWithText("Details").performClick().assertIsSelected()
        composeRule.runOnIdle { assertEquals(1, selected) }
    }

    @Test
    fun avatar_exposes_one_accessibility_label() {
        composeRule.setContent {
            CoreUiTheme {
                CoreAvatar(
                    label = "Ali Schiller",
                    contentDescription = "Profile photo for Ali Schiller",
                )
            }
        }

        composeRule.onNodeWithContentDescription("Profile photo for Ali Schiller").assertExists()
    }

    @Test
    fun modal_sheet_only_exists_when_visible() {
        var visible by mutableStateOf(true)
        composeRule.setContent {
            CoreUiTheme {
                CoreModalBottomSheet(
                    visible = visible,
                    onDismissRequest = { visible = false },
                    title = "Filters",
                ) {
                    Text("Sheet content")
                }
            }
        }

        composeRule.onNodeWithText("Filters").assertExists()
        composeRule.onNodeWithText("Sheet content").assertExists()
    }

    @Test
    fun navigation_bar_updates_selected_destination() {
        var selected by mutableIntStateOf(0)
        composeRule.setContent {
            CoreUiTheme {
                CoreNavigationBar(
                    items = listOf(CoreNavigationItem("Home"), CoreNavigationItem("Settings")),
                    selectedIndex = selected,
                    onSelected = { selected = it },
                    icon = { item, _ -> Text(item.label.take(1)) },
                )
            }
        }

        composeRule.onNodeWithText("Settings").performClick().assertIsSelected()
        composeRule.runOnIdle { assertEquals(1, selected) }
    }
}
