package com.msa.coreui.calendar

import com.msa.coreui.calendar.model.PersianDate
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.calendar.model.PersianDateSelectionMode
import com.msa.coreui.calendar.model.GregorianDateRange
import com.msa.coreui.calendar.model.PersianYearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class PersianDatePickerStateTest {

    private val minDate = PersianDate(1400, 1, 1).toGregorian()
    private val maxDate = PersianDate(1410, 12, 29).toGregorian()

    @Test
    fun navigating_from_bahman_opens_esfand() {
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 11),
        )

        state.navigateMonths(1)

        assertEquals(PersianYearMonth(1404, 12), state.displayedMonth)
    }

    @Test
    fun range_rejection_preserves_the_existing_start_date() {
        val start = PersianDate(1404, 2, 1).toGregorian()
        val disabled = PersianDate(1404, 2, 3).toGregorian()
        val end = PersianDate(1404, 2, 5).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.RANGE,
            displayedMonth = PersianYearMonth(1404, 2),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDates = setOf(disabled),
            ),
        )

        state.selectDate(start)
        val result = state.selectDateResult(end)

        assertTrue(result is PersianDateSelectionResult.Rejected)
        assertEquals(
            PersianDateSelectionRejection.RANGE_CONTAINS_DISABLED_DATE,
            (result as PersianDateSelectionResult.Rejected).reason,
        )
        assertEquals(PersianDateSelection.Range(start, null), state.selection)
        assertEquals(PersianDateSelectionRejection.RANGE_CONTAINS_DISABLED_DATE, state.lastSelectionRejection)
        assertFalse(state.canConfirm)
    }

    @Test
    fun multiple_selection_honors_configured_limit() {
        val state = createState(
            mode = PersianDateSelectionMode.MULTIPLE,
            displayedMonth = PersianYearMonth(1404, 3),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                maxMultipleSelection = 2,
            ),
        )
        val dates = (1..3).map { day -> PersianDate(1404, 3, day).toGregorian() }

        dates.forEach(state::selectDate)

        val selection = state.selection as PersianDateSelection.Multiple
        assertEquals(setOf(dates[0], dates[1]), selection.dates)
        assertTrue(state.canConfirm)
    }

    @Test
    fun unselected_date_becomes_unavailable_when_multiple_limit_is_reached() {
        val state = createState(
            mode = PersianDateSelectionMode.MULTIPLE,
            displayedMonth = PersianYearMonth(1404, 3),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                maxMultipleSelection = 1,
            ),
        )
        val first = PersianDate(1404, 3, 1).toGregorian()
        val second = PersianDate(1404, 3, 2).toGregorian()

        state.selectDate(first)

        assertTrue(state.canSelectDate(first))
        assertFalse(state.canSelectDate(second))

        state.selectDate(first)
        assertTrue(state.canSelectDate(second))
    }


    @Test
    fun reverse_range_selection_is_normalized_chronologically() {
        val earlier = PersianDate(1404, 5, 4).toGregorian()
        val later = PersianDate(1404, 5, 10).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.RANGE,
            displayedMonth = PersianYearMonth(1404, 5),
        )

        state.selectDate(later)
        state.selectDate(earlier)

        assertEquals(PersianDateSelection.Range(earlier, later), state.selection)
        assertTrue(state.canConfirm)
    }

    @Test
    fun configuration_defensively_copies_disabled_dates() {
        val disabled = PersianDate(1404, 6, 1).toGregorian()
        val mutableDates = mutableSetOf(disabled)
        val config = PersianDatePickerConfig(
            minDate = minDate,
            maxDate = maxDate,
            disabledDates = mutableDates,
        )

        mutableDates.clear()

        assertEquals(setOf(disabled), config.disabledDates)
        assertThrows(UnsupportedOperationException::class.java) {
            @Suppress("UNCHECKED_CAST")
            (config.disabledDates as MutableSet<LocalDate>).add(disabled.plusDays(1))
        }
    }

    @Test
    fun multiple_selection_exposes_an_unmodifiable_sorted_set() {
        val later = PersianDate(1404, 7, 8).toGregorian()
        val earlier = PersianDate(1404, 7, 2).toGregorian()
        val selection = PersianDateSelection.Multiple(linkedSetOf(later, earlier))

        assertEquals(listOf(earlier, later), selection.dates.toList())
        assertThrows(UnsupportedOperationException::class.java) {
            @Suppress("UNCHECKED_CAST")
            (selection.dates as MutableSet<LocalDate>).add(later.plusDays(1))
        }
    }

    @Test
    fun initial_selection_is_sanitized_against_boundaries_and_disabled_dates() {
        val valid = PersianDate(1404, 8, 2).toGregorian()
        val disabled = PersianDate(1404, 8, 3).toGregorian()
        val outside = maxDate.plusDays(1)
        val state = PersianDatePickerState(
            selectionMode = PersianDateSelectionMode.MULTIPLE,
            initialSelection = PersianDateSelection.Multiple(setOf(outside, disabled, valid)),
            initialDisplayedMonth = PersianYearMonth(1404, 8),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDates = setOf(disabled),
            ),
        )

        assertEquals(PersianDateSelection.Multiple(setOf(valid)), state.selection)
    }

    @Test
    fun disabled_date_cannot_be_selected() {
        val disabled = PersianDate(1404, 4, 10).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 4),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDates = setOf(disabled),
            ),
        )

        state.selectDate(disabled)

        assertEquals(PersianDateSelection.Single(null), state.selection)
        assertFalse(state.canConfirm)
    }

    @Test
    fun reversed_initial_range_is_normalized_chronologically() {
        val earlier = PersianDate(1404, 9, 3).toGregorian()
        val later = PersianDate(1404, 9, 12).toGregorian()
        val state = PersianDatePickerState(
            selectionMode = PersianDateSelectionMode.RANGE,
            initialSelection = PersianDateSelection.Range(later, earlier),
            initialDisplayedMonth = PersianYearMonth(1404, 9),
            config = PersianDatePickerConfig(minDate = minDate, maxDate = maxDate),
        )

        assertEquals(PersianDateSelection.Range(earlier, later), state.selection)
        assertTrue(state.canConfirm)
    }

    @Test
    fun valid_end_of_initial_range_is_preserved_when_start_is_invalid() {
        val validEnd = PersianDate(1404, 9, 12).toGregorian()
        val state = PersianDatePickerState(
            selectionMode = PersianDateSelectionMode.RANGE,
            initialSelection = PersianDateSelection.Range(maxDate.plusDays(1), validEnd),
            initialDisplayedMonth = PersianYearMonth(1404, 9),
            config = PersianDatePickerConfig(minDate = minDate, maxDate = maxDate),
        )

        assertEquals(PersianDateSelection.Range(validEnd, null), state.selection)
        assertFalse(state.canConfirm)
    }

    @Test
    fun disabled_dates_outside_boundary_are_discarded() {
        val inside = PersianDate(1404, 10, 2).toGregorian()
        val config = PersianDatePickerConfig(
            minDate = minDate,
            maxDate = maxDate,
            disabledDates = setOf(minDate.minusDays(1), inside, maxDate.plusDays(1)),
        )

        assertEquals(setOf(inside), config.disabledDates)
    }

    @Test
    fun same_day_initial_range_remains_confirmable() {
        val day = PersianDate(1404, 9, 20).toGregorian()
        val state = PersianDatePickerState(
            selectionMode = PersianDateSelectionMode.RANGE,
            initialSelection = PersianDateSelection.Range(day, day),
            initialDisplayedMonth = PersianYearMonth(1404, 9),
            config = PersianDatePickerConfig(minDate = minDate, maxDate = maxDate),
        )

        assertEquals(PersianDateSelection.Range(day, day), state.selection)
        assertTrue(state.canConfirm)
    }

    @Test
    fun replace_selection_applies_sanitization_and_rejects_wrong_mode() {
        val valid = PersianDate(1404, 11, 2).toGregorian()
        val outside = maxDate.plusDays(1)
        val state = createState(
            mode = PersianDateSelectionMode.MULTIPLE,
            displayedMonth = PersianYearMonth(1404, 11),
        )

        state.replaceSelection(PersianDateSelection.Multiple(setOf(outside, valid)))
        assertEquals(PersianDateSelection.Multiple(setOf(valid)), state.selection)

        assertThrows(IllegalArgumentException::class.java) {
            state.replaceSelection(PersianDateSelection.Single(valid))
        }
    }


    @Test
    fun range_can_include_disabled_dates_when_explicitly_allowed() {
        val start = PersianDate(1404, 2, 1).toGregorian()
        val disabled = PersianDate(1404, 2, 3).toGregorian()
        val end = PersianDate(1404, 2, 5).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.RANGE,
            displayedMonth = PersianYearMonth(1404, 2),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDates = setOf(disabled),
                allowDisabledDatesInsideRange = true,
            ),
        )

        state.selectDate(start)
        state.selectDate(end)

        assertEquals(PersianDateSelection.Range(start, end), state.selection)
        assertTrue(state.canConfirm)
    }

    @Test
    fun empty_selection_can_be_confirmed_when_explicitly_allowed() {
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 1),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                allowEmptySelectionConfirmation = true,
            ),
        )

        assertFalse(state.hasSelection)
        assertTrue(state.canConfirm)
    }

    @Test
    fun adjacent_month_navigation_can_be_disabled_without_disabling_month_buttons() {
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 1),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                autoNavigateAdjacentMonth = false,
            ),
        )

        assertTrue(state.canNavigateNext)
        state.navigateMonths(1)
        assertEquals(PersianYearMonth(1404, 2), state.displayedMonth)
    }

    @Test
    fun selecting_boundary_year_clamps_unavailable_month() {
        val boundaryStart = PersianDate(1404, 7, 10).toGregorian()
        val boundaryEnd = PersianDate(1406, 3, 20).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1405, 12),
            config = PersianDatePickerConfig(minDate = boundaryStart, maxDate = boundaryEnd),
        )

        state.selectYear(1406)

        assertEquals(PersianYearMonth(1406, 3), state.displayedMonth)
        assertEquals(PersianDatePickerView.MONTHS, state.view)
    }

    @Test
    fun date_ranges_and_weekdays_are_enforced_with_specific_reasons() {
        val rangeStart = PersianDate(1404, 3, 5).toGregorian()
        val rangeEnd = PersianDate(1404, 3, 8).toGregorian()
        val friday = generateSequence(rangeEnd.plusDays(1)) { it.plusDays(1) }
            .first { it.dayOfWeek == DayOfWeek.FRIDAY }
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 3),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDateRanges = listOf(GregorianDateRange(rangeStart, rangeEnd)),
                disabledWeekdays = setOf(DayOfWeek.FRIDAY),
            ),
        )

        assertEquals(PersianDateAvailability.DISABLED_RANGE, state.dateAvailability(rangeStart))
        assertEquals(PersianDateAvailability.DISABLED_RANGE, state.dateAvailability(rangeEnd))
        assertEquals(PersianDateAvailability.DISABLED_WEEKDAY, state.dateAvailability(friday))
        assertFalse(state.trySelectDate(rangeStart))
        assertEquals(PersianDateSelection.Single(null), state.selection)
    }

    @Test
    fun adjacent_disabled_ranges_are_normalized_and_defensively_copied() {
        val first = GregorianDateRange(
            PersianDate(1404, 4, 1).toGregorian(),
            PersianDate(1404, 4, 3).toGregorian(),
        )
        val second = GregorianDateRange(
            first.endInclusive.plusDays(1),
            PersianDate(1404, 4, 7).toGregorian(),
        )
        val mutableRanges = mutableListOf(first, second)
        val mutableWeekdays = mutableSetOf(DayOfWeek.FRIDAY)
        val config = PersianDatePickerConfig(
            minDate = minDate,
            maxDate = maxDate,
            disabledDateRanges = mutableRanges,
            disabledWeekdays = mutableWeekdays,
        )

        mutableRanges.clear()
        mutableWeekdays.clear()

        assertEquals(listOf(GregorianDateRange(first.start, second.endInclusive)), config.disabledDateRanges)
        assertEquals(setOf(DayOfWeek.FRIDAY), config.disabledWeekdays)
        assertThrows(UnsupportedOperationException::class.java) {
            @Suppress("UNCHECKED_CAST")
            (config.disabledDateRanges as MutableList<GregorianDateRange>).clear()
        }
    }

    @Test
    fun range_selection_rejection_preserves_start_when_disabled_weekday_is_inside() {
        val start = LocalDate.of(2025, 1, 1)
        val end = start.plusDays(10)
        val disabledWeekday = start.plusDays(1).dayOfWeek
        val state = createState(
            mode = PersianDateSelectionMode.RANGE,
            displayedMonth = PersianYearMonth.fromGregorian(start),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledWeekdays = setOf(disabledWeekday),
            ),
        )

        state.selectDate(start)
        val result = state.selectDateResult(end)

        assertTrue(result is PersianDateSelectionResult.Rejected)
        assertEquals(PersianDateSelection.Range(start, null), state.selection)
        assertEquals(PersianDateSelectionRejection.RANGE_CONTAINS_DISABLED_DATE, state.lastSelectionRejection)
    }

    @Test
    fun fully_disabled_months_are_skipped_during_navigation() {
        val disabledMonth = PersianYearMonth(1404, 2)
        val disabledRange = GregorianDateRange(
            disabledMonth.firstDay.toGregorian(),
            disabledMonth.atDay(disabledMonth.lengthOfMonth).toGregorian(),
        )
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 1),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDateRanges = listOf(disabledRange),
                skipFullyDisabledMonths = true,
            ),
        )

        assertFalse(state.isMonthSelectable(1404, 2))
        assertTrue(state.tryNavigateMonths(1))
        assertEquals(PersianYearMonth(1404, 3), state.displayedMonth)
    }

    @Test
    fun initial_displayed_month_moves_away_from_a_fully_disabled_month() {
        val disabledMonth = PersianYearMonth(1404, 2)
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = disabledMonth,
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDateRanges = listOf(
                    GregorianDateRange(
                        disabledMonth.firstDay.toGregorian(),
                        disabledMonth.atDay(disabledMonth.lengthOfMonth).toGregorian(),
                    ),
                ),
                skipFullyDisabledMonths = true,
            ),
        )

        assertEquals(PersianYearMonth(1404, 1), state.displayedMonth)
        assertTrue(state.hasSelectableDates)
    }

    @Test
    fun rejected_selection_exposes_a_typed_reason_and_can_be_cleared() {
        val disabled = PersianDate(1404, 4, 10).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 4),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                disabledDates = setOf(disabled),
            ),
        )

        val result = state.selectDateResult(disabled)

        assertEquals(PersianDateSelectionRejection.DISABLED_DATE, (result as PersianDateSelectionResult.Rejected).reason)
        assertEquals(PersianDateSelectionRejection.DISABLED_DATE, state.lastSelectionRejection)
        state.clearSelectionFeedback()
        assertEquals(null, state.lastSelectionRejection)
    }

    @Test
    fun availability_reports_multiple_limit_without_disabling_selected_dates() {
        val first = PersianDate(1404, 5, 1).toGregorian()
        val second = first.plusDays(1)
        val state = createState(
            mode = PersianDateSelectionMode.MULTIPLE,
            displayedMonth = PersianYearMonth(1404, 5),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                maxMultipleSelection = 1,
            ),
        )

        assertTrue(state.trySelectDate(first))
        assertEquals(PersianDateAvailability.SELECTABLE, state.dateAvailability(first))
        assertEquals(PersianDateAvailability.MULTIPLE_SELECTION_LIMIT, state.dateAvailability(second))
        assertFalse(state.trySelectDate(second))
        assertTrue(state.trySelectDate(first))
    }

    @Test
    fun nearest_selectable_date_skips_fully_disabled_month_and_chooses_closest_boundary() {
        val disabledMonth = PersianYearMonth(1404, 6)
        val previousDay = disabledMonth.firstDay.toGregorian().minusDays(1)
        val nextDay = disabledMonth.atDay(disabledMonth.lengthOfMonth).toGregorian().plusDays(1)
        val origin = disabledMonth.atDay(2).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = disabledMonth,
            config = PersianDatePickerConfig(
                minDate = previousDay.minusMonths(1),
                maxDate = nextDay.plusMonths(1),
                disabledDateRanges = listOf(
                    GregorianDateRange(
                        disabledMonth.firstDay.toGregorian(),
                        disabledMonth.atDay(disabledMonth.lengthOfMonth).toGregorian(),
                    ),
                ),
                skipFullyDisabledMonths = true,
            ),
        )

        assertEquals(previousDay, state.nearestSelectableDate(origin))
    }

    @Test
    fun all_weekdays_disabled_produces_no_selectable_date_without_scanning_forever() {
        val start = PersianDate(1404, 1, 1).toGregorian()
        val end = PersianDate(1404, 1, 31).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 1),
            config = PersianDatePickerConfig(
                minDate = start,
                maxDate = end,
                disabledWeekdays = DayOfWeek.entries.toSet(),
                skipFullyDisabledMonths = true,
            ),
        )

        assertFalse(state.hasSelectableDates)
        assertEquals(null, state.nearestSelectableDate(start.plusDays(10)))
        assertFalse(state.canNavigatePrevious)
        assertFalse(state.canNavigateNext)
    }

    @Test
    fun range_shorter_than_minimum_is_rejected_without_losing_start() {
        val start = PersianDate(1404, 4, 10).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.RANGE,
            displayedMonth = PersianYearMonth(1404, 4),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                minRangeDays = 3,
            ),
        )

        state.selectDate(start)
        val result = state.selectDateResult(start.plusDays(1))

        assertEquals(PersianDateSelectionRejection.RANGE_TOO_SHORT, (result as PersianDateSelectionResult.Rejected).reason)
        assertEquals(PersianDateSelection.Range(start, null), state.selection)
        assertFalse(state.canConfirm)
    }

    @Test
    fun range_longer_than_maximum_is_rejected() {
        val start = PersianDate(1404, 4, 10).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.RANGE,
            displayedMonth = PersianYearMonth(1404, 4),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                maxRangeDays = 5,
            ),
        )

        state.selectDate(start)
        val result = state.selectDateResult(start.plusDays(5))

        assertEquals(PersianDateSelectionRejection.RANGE_TOO_LONG, (result as PersianDateSelectionResult.Rejected).reason)
        assertEquals(PersianDateSelection.Range(start, null), state.selection)
    }

    @Test
    fun multiple_selection_requires_configured_minimum_before_confirmation() {
        val state = createState(
            mode = PersianDateSelectionMode.MULTIPLE,
            displayedMonth = PersianYearMonth(1404, 5),
            config = PersianDatePickerConfig(
                minDate = minDate,
                maxDate = maxDate,
                minMultipleSelection = 2,
                maxMultipleSelection = 4,
            ),
        )
        val first = PersianDate(1404, 5, 1).toGregorian()
        val second = first.plusDays(1)

        state.selectDate(first)
        assertFalse(state.canConfirm)
        state.selectDate(second)
        assertTrue(state.canConfirm)
    }

    @Test
    fun invalid_selection_constraints_are_rejected_by_config() {
        assertThrows(IllegalArgumentException::class.java) {
            PersianDatePickerConfig(minMultipleSelection = 3, maxMultipleSelection = 2)
        }
        assertThrows(IllegalArgumentException::class.java) {
            PersianDatePickerConfig(minRangeDays = 4, maxRangeDays = 3)
        }
        assertThrows(IllegalArgumentException::class.java) {
            PersianDatePickerConfig(
                minDate = LocalDate.of(2025, 1, 1),
                maxDate = LocalDate.of(2025, 1, 2),
                minMultipleSelection = 3,
            )
        }
    }


    @Test
    fun empty_selection_can_be_confirmed_when_no_dates_are_selectable_and_empty_is_allowed() {
        val start = PersianDate(1404, 1, 1).toGregorian()
        val end = PersianDate(1404, 1, 31).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 1),
            config = PersianDatePickerConfig(
                minDate = start,
                maxDate = end,
                disabledWeekdays = DayOfWeek.entries.toSet(),
                allowEmptySelectionConfirmation = true,
            ),
        )

        assertEquals(PersianDateConfirmationStatus.EMPTY_SELECTION_ALLOWED, state.confirmationStatus)
        assertTrue(state.canConfirm)
    }

    @Test
    fun overshooting_navigation_falls_back_to_the_nearest_selectable_month_in_requested_direction() {
        val firstMonthStart = PersianDate(1404, 1, 1).toGregorian()
        val firstMonthEnd = PersianDate(1404, 1, 31).toGregorian()
        val fourthMonthStart = PersianDate(1404, 4, 1).toGregorian()
        val fourthMonthEnd = PersianDate(1404, 4, 31).toGregorian()
        val config = PersianDatePickerConfig(
            minDate = firstMonthStart,
            maxDate = fourthMonthEnd,
            disabledDateRanges = listOf(
                GregorianDateRange(firstMonthStart, firstMonthEnd),
                GregorianDateRange(fourthMonthStart, fourthMonthEnd),
            ),
            skipFullyDisabledMonths = true,
        )

        val backward = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 3),
            config = config,
        )
        assertTrue(backward.tryNavigateMonths(-100))
        assertEquals(PersianYearMonth(1404, 2), backward.displayedMonth)

        val forward = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 2),
            config = config,
        )
        assertTrue(forward.tryNavigateMonths(100))
        assertEquals(PersianYearMonth(1404, 3), forward.displayedMonth)
    }

    @Test
    fun explicitly_disabling_the_entire_boundary_short_circuits_selectability() {
        val start = PersianDate(PersianCalendar.MIN_SUPPORTED_YEAR, 1, 1).toGregorian()
        val maxYear = PersianCalendar.MAX_SUPPORTED_YEAR
        val end = PersianDate(maxYear, 12, PersianCalendar.lengthOfMonth(maxYear, 12)).toGregorian()
        val state = createState(
            mode = PersianDateSelectionMode.SINGLE,
            displayedMonth = PersianYearMonth(1404, 1),
            config = PersianDatePickerConfig(
                minDate = start,
                maxDate = end,
                disabledDateRanges = listOf(GregorianDateRange(start, end)),
                skipFullyDisabledMonths = true,
            ),
        )

        assertFalse(state.hasSelectableDates)
        assertFalse(state.canNavigatePrevious)
        assertFalse(state.canNavigateNext)
        assertEquals(null, state.nearestSelectableDate(PersianDate(1404, 1, 1).toGregorian()))
    }

    private fun createState(
        mode: PersianDateSelectionMode,
        displayedMonth: PersianYearMonth,
        config: PersianDatePickerConfig = PersianDatePickerConfig(
            minDate = minDate,
            maxDate = maxDate,
        ),
    ): PersianDatePickerState = PersianDatePickerState(
        selectionMode = mode,
        initialSelection = PersianDateSelection.empty(mode),
        initialDisplayedMonth = displayedMonth,
        config = config,
    )
}

class PersianDateConfirmationStatusTest {
    private val minDate = java.time.LocalDate.of(2024, 1, 1)
    private val maxDate = java.time.LocalDate.of(2024, 12, 31)

    @org.junit.Test
    fun confirmation_status_distinguishes_empty_incomplete_and_ready_range() {
        val state = PersianDatePickerState(
            selectionMode = com.msa.coreui.calendar.model.PersianDateSelectionMode.RANGE,
            initialSelection = com.msa.coreui.calendar.model.PersianDateSelection.Range(null, null),
            initialDisplayedMonth = com.msa.coreui.calendar.model.PersianYearMonth.fromGregorian(minDate),
            config = PersianDatePickerConfig(minDate = minDate, maxDate = maxDate),
        )

        org.junit.Assert.assertEquals(
            PersianDateConfirmationStatus.EMPTY_SELECTION_REQUIRED,
            state.confirmationStatus,
        )
        state.selectDate(minDate)
        org.junit.Assert.assertEquals(
            PersianDateConfirmationStatus.RANGE_END_REQUIRED,
            state.confirmationStatus,
        )
        state.selectDate(minDate.plusDays(1))
        org.junit.Assert.assertEquals(PersianDateConfirmationStatus.READY, state.confirmationStatus)
    }

    @org.junit.Test
    fun config_rejects_maximum_range_longer_than_boundary() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            PersianDatePickerConfig(
                minDate = minDate,
                maxDate = minDate.plusDays(4),
                minRangeDays = 1,
                maxRangeDays = 6,
            )
        }
    }
}
