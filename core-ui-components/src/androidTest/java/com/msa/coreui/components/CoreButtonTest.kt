package com.msa.coreui.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.msa.coreui.foundation.CoreUiTheme
import org.junit.Rule
import org.junit.Test

class CoreButtonTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun loadingButton_isDisabledAndKeepsLabel() {
        composeRule.setContent {
            CoreUiTheme {
                CoreButton(
                    content = CoreButtonContent("Save"),
                    onClick = {},
                    loading = true,
                )
            }
        }

        composeRule.onNodeWithText("Save").assertExists()
        composeRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun enabledButton_isEnabled() {
        composeRule.setContent {
            CoreUiTheme {
                CoreButton(CoreButtonContent("Continue"), onClick = {})
            }
        }
        composeRule.onNodeWithText("Continue").assertIsEnabled()
    }
}
