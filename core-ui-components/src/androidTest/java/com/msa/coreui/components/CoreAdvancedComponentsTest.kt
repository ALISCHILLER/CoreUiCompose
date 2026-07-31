package com.msa.coreui.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.msa.coreui.foundation.CoreUiTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CoreAdvancedComponentsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun search_field_clear_action_updates_controlled_query() {
        var query = "calendar"
        composeRule.setContent {
            CoreUiTheme {
                CoreSearchField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Clear").performClick()
        composeRule.runOnIdle { assertEquals("", query) }
    }

    @Test
    fun badge_exposes_accessibility_description() {
        composeRule.setContent {
            CoreUiTheme {
                CoreBadge(
                    text = "3",
                    contentDescription = "3 unread notifications",
                    style = CoreBadgeStyle.INFO,
                )
            }
        }

        composeRule.onNodeWithContentDescription("3 unread notifications").assertExists()
    }

    @Test
    fun async_content_renders_only_the_active_branch() {
        composeRule.setContent {
            CoreUiTheme {
                CoreAsyncContent(
                    state = CoreAsyncState.Success("Ready"),
                    content = { Text(it) },
                    empty = { Text("Empty") },
                )
            }
        }

        composeRule.onNodeWithText("Ready").assertExists()
    }
}

class CoreInteractionComponentsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun segmented_control_reports_controlled_selection_changes() {
        var selected = 0
        composeRule.setContent {
            CoreUiTheme {
                CoreSegmentedControl(
                    items = listOf(CoreSegmentItem("Day"), CoreSegmentItem("Week")),
                    selectedIndex = selected,
                    onSelected = { selected = it },
                )
            }
        }

        composeRule.onNodeWithText("Week").performClick()
        composeRule.runOnIdle { assertEquals(1, selected) }
    }

    @Test
    fun password_field_exposes_an_accessible_visibility_action() {
        composeRule.setContent {
            CoreUiTheme {
                CorePasswordField(
                    value = "secret",
                    onValueChange = {},
                    label = "Password",
                    passwordVisible = false,
                    onPasswordVisibilityChange = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Show password").assertExists()
    }

    @Test
    fun expandable_card_keeps_expansion_owned_by_the_caller() {
        var requested = false
        composeRule.setContent {
            CoreUiTheme {
                CoreExpandableCard(
                    title = "Advanced",
                    expanded = false,
                    onExpandedChange = { requested = it },
                ) {
                    Text("Details")
                }
            }
        }

        composeRule.onNodeWithText("Advanced").performClick()
        composeRule.runOnIdle { assertEquals(true, requested) }
    }


    @Test
    fun disabled_expandable_card_does_not_expose_an_active_action() {
        var requested = false
        composeRule.setContent {
            CoreUiTheme {
                CoreExpandableCard(
                    title = "Locked section",
                    expanded = false,
                    enabled = false,
                    onExpandedChange = { requested = true },
                ) {
                    Text("Hidden details")
                }
            }
        }

        composeRule.onNodeWithText("Locked section").assertIsNotEnabled()
        composeRule.runOnIdle { assertEquals(false, requested) }
    }

    @Test
    fun avatar_presence_is_announced_once() {
        composeRule.setContent {
            CoreUiTheme {
                CoreAvatar(
                    label = "Ali Schiller",
                    status = CoreAvatarStatus.ONLINE,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Ali Schiller, Online").assertExists()
    }

    @Test
    fun select_field_reports_the_selected_value_to_the_caller() {
        var selected = "tehran"
        composeRule.setContent {
            CoreUiTheme {
                CoreSelectField(
                    options = listOf(
                        CoreSelectOption("tehran", "Tehran"),
                        CoreSelectOption("tabriz", "Tabriz"),
                    ),
                    selectedValue = selected,
                    onValueSelected = { selected = it },
                    label = "Region",
                )
            }
        }

        composeRule.onNodeWithContentDescription("Region").performClick()
        composeRule.onNodeWithContentDescription("Tabriz").performClick()
        composeRule.runOnIdle { assertEquals("tabriz", selected) }
    }


    @Test
    fun select_field_handles_an_empty_async_option_list_without_crashing() {
        composeRule.setContent {
            CoreUiTheme {
                CoreSelectField<String>(
                    options = emptyList(),
                    selectedValue = null,
                    onValueSelected = {},
                    label = "Region",
                )
            }
        }

        composeRule.onNodeWithContentDescription("Region").assertIsNotEnabled()
    }

    @Test
    fun text_field_surfaces_external_length_violations_instead_of_crashing() {
        composeRule.setContent {
            CoreUiTheme {
                CoreTextField(
                    value = "abcdef",
                    onValueChange = {},
                    label = "Code",
                    maxLength = 4,
                )
            }
        }

        composeRule.onNodeWithText("2 characters over the limit").assertExists()
    }

    @Test
    fun validation_summary_issue_can_request_focus_for_its_field() {
        var clickedIndex = -1
        composeRule.setContent {
            CoreUiTheme {
                CoreValidationSummary(
                    issues = listOf(
                        CoreValidationIssue("Email", "Enter a valid address"),
                        CoreValidationIssue("Password", "Use at least 12 characters"),
                    ),
                    onIssueClick = { clickedIndex = it },
                )
            }
        }

        composeRule.onNodeWithText("Password").performClick()
        composeRule.runOnIdle { assertEquals(1, clickedIndex) }
    }

    @Test
    fun state_view_action_remains_independently_clickable() {
        var retried = false
        composeRule.setContent {
            CoreUiTheme {
                CoreStateView(
                    title = "Could not load",
                    message = "Check the connection.",
                    actionLabel = "Retry",
                    onAction = { retried = true },
                    style = CoreStateStyle.ERROR,
                    accessibilityLabel = "Loading failed",
                )
            }
        }

        composeRule.onNodeWithText("Retry").performClick()
        composeRule.runOnIdle { assertEquals(true, retried) }
    }

}
