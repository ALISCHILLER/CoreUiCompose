package com.msa.coreui.catalog

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CatalogAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeScreenMatchesShowcaseNavigationContract() {
        val storage = InMemoryCatalogSettingsStorage()
        composeRule.setContent { CatalogRoot(storage) }

        composeRule.onNodeWithTag(CatalogTestTags.HOME_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("MSA Core UI").assertIsDisplayed()
        composeRule.onNodeWithText("کاتالوگ کامپوننت‌ها").assertIsDisplayed()
        composeRule.onNodeWithTag(CatalogTestTags.category("inputs")).assertIsDisplayed()
        composeRule.onNodeWithTag(CatalogTestTags.category("feedback")).assertIsDisplayed()
    }

    @Test
    fun inputCategoryOpensProductionForm() {
        val storage = InMemoryCatalogSettingsStorage()
        composeRule.setContent { CatalogRoot(storage) }

        composeRule.onNodeWithTag(CatalogTestTags.category("inputs")).performClick()

        composeRule.onNodeWithTag(CatalogTestTags.COMPONENTS_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("فرم‌ها و ورودی‌ها").assertIsDisplayed()
        composeRule.onNodeWithText("ذخیره").assertIsDisplayed()
    }

    @Test
    fun navigationCategoryOpensInteractiveNavigationShowcase() {
        val storage = InMemoryCatalogSettingsStorage()
        composeRule.setContent { CatalogRoot(storage) }

        composeRule.onNodeWithTag(CatalogTestTags.HOME_SCREEN)
            .performScrollToNode(hasTestTag(CatalogTestTags.category("navigation")))
        composeRule.onNodeWithTag(CatalogTestTags.category("navigation")).performClick()

        composeRule.onNodeWithTag(CatalogTestTags.NAVIGATION_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("الگوهای ناوبری").assertIsDisplayed()
        composeRule.onNodeWithText("نوار ناوبری پایین").assertIsDisplayed()
    }

    @Test
    fun layoutCategoryOpensResponsiveLayoutShowcase() {
        val storage = InMemoryCatalogSettingsStorage()
        composeRule.setContent { CatalogRoot(storage) }

        composeRule.onNodeWithTag(CatalogTestTags.HOME_SCREEN)
            .performScrollToNode(hasTestTag(CatalogTestTags.category("layout")))
        composeRule.onNodeWithTag(CatalogTestTags.category("layout")).performClick()

        composeRule.onNodeWithTag(CatalogTestTags.LAYOUT_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("کلاس عرض فعلی").assertIsDisplayed()
    }

    @Test
    fun headerSettingsActionOpensLiveThemeScreen() {
        val storage = InMemoryCatalogSettingsStorage()
        composeRule.setContent { CatalogRoot(storage) }

        composeRule.onNodeWithTag(CatalogTestTags.HEADER_SETTINGS).performClick()

        composeRule.onNodeWithTag(CatalogTestTags.SETTINGS_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("رنگ اصلی").assertIsDisplayed()
        composeRule.onNodeWithText("زبان و جهت").assertIsDisplayed()
    }


    @Test
    fun navigationStateSurvivesSavedInstanceRestoration() {
        val restorationTester = StateRestorationTester(composeRule)
        val storage = InMemoryCatalogSettingsStorage()
        restorationTester.setContent {
            CatalogRoot(storage)
        }

        composeRule.onNodeWithTag(CatalogTestTags.category("inputs")).performClick()
        composeRule.onNodeWithTag(CatalogTestTags.COMPONENTS_SCREEN).assertIsDisplayed()

        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithTag(CatalogTestTags.COMPONENTS_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("فرم‌ها و ورودی‌ها").assertIsDisplayed()
    }

    @Test
    fun languageSelectionUpdatesDirectionCopyAndPersistsPreference() {
        val storage = InMemoryCatalogSettingsStorage()
        composeRule.setContent { CatalogRoot(storage) }

        composeRule.onNodeWithTag(CatalogTestTags.HEADER_SETTINGS).performClick()
        composeRule.onNodeWithText("English").performClick()

        composeRule.onNodeWithText("Primary color").assertIsDisplayed()
        composeRule.waitForIdle()
        assertEquals(CatalogLanguageMode.ENGLISH, storage.current.languageMode)
    }
}


private class InMemoryCatalogSettingsStorage(
    initial: CatalogSettings = CatalogSettings(),
) : CatalogSettingsStorage {
    var current: CatalogSettings = initial
        private set

    override fun load(): CatalogSettings = current

    override fun save(settings: CatalogSettings) {
        current = settings
    }

    override fun clear() {
        current = CatalogSettings()
    }
}
