package com.msa.coreui.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.msa.coreui.foundation.CoreUiMotion
import com.msa.coreui.foundation.CoreUiTheme
import com.msa.coreui.loading.LoadingThreeDots
import org.junit.Rule
import org.junit.Test

class CoreComponentsSemanticsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun icon_button_requires_and_exposes_action_label() {
        composeRule.setContent {
            CoreUiTheme {
                CoreIconButton(onClick = {}, contentDescription = "Close dialog") {
                    Text("×")
                }
            }
        }

        composeRule.onNodeWithContentDescription("Close dialog").assertIsEnabled()
    }

    @Test
    fun selected_chip_exposes_selected_semantics() {
        composeRule.setContent {
            CoreUiTheme {
                CoreChip(label = "Active", onClick = {}, selected = true)
            }
        }

        composeRule.onNodeWithText("Active").assertIsSelected()
    }

    @Test
    fun reduced_motion_loading_indicator_keeps_progress_semantics() {
        composeRule.setContent {
            CoreUiTheme(motion = CoreUiMotion(animationsEnabled = false)) {
                LoadingThreeDots(contentDescription = "Refreshing")
            }
        }

        composeRule.onNodeWithContentDescription("Refreshing").assertExists()
    }

    @Test
    fun determinate_progress_exposes_description() {
        composeRule.setContent {
            CoreUiTheme {
                CoreLinearProgress(progress = 0.75f, contentDescription = "Uploading")
            }
        }

        composeRule.onNodeWithContentDescription("Uploading").assertExists()
    }
}
