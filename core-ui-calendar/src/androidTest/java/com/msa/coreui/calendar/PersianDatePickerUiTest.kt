package com.msa.coreui.calendar

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.msa.coreui.calendar.model.PersianDate
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.calendar.model.PersianDateSelectionMode
import com.msa.coreui.foundation.CoreUiTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class PersianDatePickerUiTest {

    @get:Rule
    val composeRule = createComposeRule()


    @Test
    fun minimum_supported_month_renders_without_converting_out_of_range_adjacent_days() {
        val minimum = PersianDate(-61, 1, 1)
        val maximum = PersianDate(-61, 2, 1)

        composeRule.setContent {
            val state = rememberPersianDatePickerState(
                selectionMode = PersianDateSelectionMode.SINGLE,
                initialDisplayedDate = minimum.toGregorian(),
                config = PersianDatePickerConfig(
                    minDate = minimum.toGregorian(),
                    maxDate = maximum.toGregorian(),
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(
                state = state,
                strings = PersianDatePickerStrings.English,
            )
        }

        composeRule
            .onNodeWithContentDescription("1 Farvardin -61")
            .assertExists()
    }

    @Test
    fun selecting_a_day_updates_hoisted_state() {
        val targetDate = LocalDate.of(2024, 3, 20)
        lateinit var pickerState: PersianDatePickerState

        composeRule.setContent {
            pickerState = rememberPersianDatePickerState(
                selectionMode = PersianDateSelectionMode.SINGLE,
                initialDisplayedDate = targetDate,
                config = PersianDatePickerConfig(
                    minDate = targetDate.minusMonths(1),
                    maxDate = targetDate.plusMonths(1),
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(
                state = pickerState,
                strings = PersianDatePickerStrings.English,
            )
        }

        val persian = PersianDate.fromGregorian(targetDate)
        composeRule
            .onNodeWithContentDescription("${persian.day} Farvardin ${persian.year}")
            .performClick()

        composeRule.runOnIdle {
            assertEquals(PersianDateSelection.Single(targetDate), pickerState.selection)
        }
    }
    @Test
    fun date_indicator_is_exposed_visually_and_to_accessibility() {
        val targetDate = LocalDate.of(2024, 3, 20)

        composeRule.setContent {
            val state = rememberPersianDatePickerState(
                selectionMode = PersianDateSelectionMode.SINGLE,
                initialDisplayedDate = targetDate,
                config = PersianDatePickerConfig(
                    minDate = targetDate.minusMonths(1),
                    maxDate = targetDate.plusMonths(1),
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(
                state = state,
                strings = PersianDatePickerStrings.English,
                dateIndicatorCount = { date -> if (date == targetDate) 2 else 0 },
                dateAdditionalDescription = { date ->
                    if (date == targetDate) "2 saved items" else null
                },
            )
        }

        composeRule
            .onNodeWithContentDescription("1 Farvardin 1403, 2 indicators, 2 saved items")
            .assertExists()
    }


    @Test
    fun stable_test_tags_expose_root_grid_and_dates() {
        val targetDate = LocalDate.of(2024, 3, 20)

        composeRule.setContent {
            val state = rememberPersianDatePickerState(
                initialDisplayedDate = targetDate,
                config = PersianDatePickerConfig(
                    minDate = targetDate.minusMonths(1),
                    maxDate = targetDate.plusMonths(1),
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(state = state, strings = PersianDatePickerStrings.English)
        }

        composeRule.onNodeWithTag(PersianDatePickerTestTags.ROOT).assertExists()
        composeRule.onNodeWithTag(PersianDatePickerTestTags.DAY_GRID).assertExists()
        composeRule.onNodeWithTag(PersianDatePickerTestTags.day(targetDate)).assertExists()
    }


    @Test
    fun disabled_date_exposes_specific_accessibility_reason() {
        val targetDate = LocalDate.of(2024, 3, 20)

        composeRule.setContent {
            val state = rememberPersianDatePickerState(
                initialDisplayedDate = targetDate,
                config = PersianDatePickerConfig(
                    minDate = targetDate.minusMonths(1),
                    maxDate = targetDate.plusMonths(1),
                    disabledDates = setOf(targetDate),
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(state = state, strings = PersianDatePickerStrings.English)
        }

        composeRule
            .onNodeWithTag(PersianDatePickerTestTags.day(targetDate))
            .assertIsNotEnabled()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    PersianDatePickerStrings.English.disabledDateLabel,
                ),
            )
    }

    @Test
    fun weekday_headers_follow_configured_first_day_of_week() {
        val targetDate = LocalDate.of(2024, 3, 20)

        composeRule.setContent {
            val state = rememberPersianDatePickerState(
                initialDisplayedDate = targetDate,
                config = PersianDatePickerConfig(
                    minDate = targetDate.minusMonths(1),
                    maxDate = targetDate.plusMonths(1),
                    firstDayOfWeek = java.time.DayOfWeek.MONDAY,
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(state = state, strings = PersianDatePickerStrings.English)
        }

        composeRule.onNodeWithContentDescription("Monday").assertExists()
        composeRule.onNodeWithContentDescription("Sunday").assertExists()
    }


    @Test
    fun convenience_field_opens_dialog_and_exposes_controlled_value() {
        val selectedDate = LocalDate.of(2024, 3, 20)

        composeRule.setContent {
            CoreUiTheme {
                PersianDatePickerField(
                    selection = PersianDateSelection.Single(selectedDate),
                    onSelectionChange = {},
                    label = "Birth date",
                    contentDescription = "Choose birth date",
                    strings = PersianDatePickerStrings.English,
                    config = PersianDatePickerConfig(
                        minDate = selectedDate.minusYears(1),
                        maxDate = selectedDate.plusYears(1),
                        digitStyle = PersianDateDigitStyle.LATIN,
                    ),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Choose birth date").performClick()
        composeRule.onNodeWithText("Select date").assertExists()
    }

    @Test
    fun rejected_range_keeps_the_start_and_announces_the_reason() {
        val start = LocalDate.of(2024, 3, 20)
        val disabled = start.plusDays(2)
        val end = start.plusDays(4)
        lateinit var pickerState: PersianDatePickerState

        composeRule.setContent {
            pickerState = rememberPersianDatePickerState(
                selectionMode = PersianDateSelectionMode.RANGE,
                initialDisplayedDate = start,
                config = PersianDatePickerConfig(
                    minDate = start.minusMonths(1),
                    maxDate = end.plusMonths(1),
                    disabledDates = setOf(disabled),
                    digitStyle = PersianDateDigitStyle.LATIN,
                ),
            )
            PersianDatePicker(
                state = pickerState,
                strings = PersianDatePickerStrings.English,
            )
        }

        composeRule.onNodeWithTag(PersianDatePickerTestTags.day(start)).performClick()
        composeRule.onNodeWithTag(PersianDatePickerTestTags.day(end)).performClick()

        composeRule.onNodeWithText(PersianDatePickerStrings.English.rangeContainsDisabledDateLabel).assertExists()
        composeRule.runOnIdle {
            assertEquals(PersianDateSelection.Range(start, null), pickerState.selection)
        }
    }

    @Test
    fun a_fully_disabled_month_is_disabled_in_the_month_selector() {
        val month = com.msa.coreui.calendar.model.PersianYearMonth(1403, 2)
        val state = PersianDatePickerState(
            selectionMode = PersianDateSelectionMode.SINGLE,
            initialSelection = PersianDateSelection.Single(null),
            initialDisplayedMonth = com.msa.coreui.calendar.model.PersianYearMonth(1403, 1),
            config = PersianDatePickerConfig(
                minDate = com.msa.coreui.calendar.model.PersianDate(1403, 1, 1).toGregorian(),
                maxDate = com.msa.coreui.calendar.model.PersianDate(1403, 3, 31).toGregorian(),
                disabledDateRanges = listOf(
                    com.msa.coreui.calendar.model.GregorianDateRange(
                        month.firstDay.toGregorian(),
                        month.atDay(month.lengthOfMonth).toGregorian(),
                    ),
                ),
            ),
        ).also(PersianDatePickerState::showMonths)

        composeRule.setContent {
            PersianDatePicker(state = state, strings = PersianDatePickerStrings.English)
        }

        composeRule.onNodeWithTag(PersianDatePickerTestTags.month(2)).assertIsNotEnabled()
    }

}
