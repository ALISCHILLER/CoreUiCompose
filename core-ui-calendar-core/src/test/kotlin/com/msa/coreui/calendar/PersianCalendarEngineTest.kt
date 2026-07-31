package com.msa.coreui.calendar

import com.msa.coreui.calendar.internal.PersianCalendarEngine
import com.msa.coreui.calendar.model.PersianDate
import com.msa.coreui.calendar.model.PersianYearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PersianCalendarEngineTest {

    @Test
    fun nowruz_1403_matches_gregorian_date() {
        assertEquals(
            PersianDate(1403, 1, 1),
            PersianDate.fromGregorian(LocalDate.of(2024, 3, 20)),
        )
    }

    @Test
    fun leap_esfand_round_trips_correctly() {
        val persian = PersianDate(1403, 12, 30)
        val gregorian = LocalDate.of(2025, 3, 20)

        assertTrue(persian.isLeapYear)
        assertEquals(gregorian, persian.toGregorian())
        assertEquals(persian, PersianDate.fromGregorian(gregorian))
    }

    @Test
    fun non_leap_esfand_rejects_day_30() {
        assertFalse(PersianDate(1404, 1, 1).isLeapYear)
        assertThrows(IllegalArgumentException::class.java) {
            PersianDate(1404, 12, 30)
        }
    }

    @Test
    fun conversions_round_trip_across_representative_years() {
        val samples = listOf(
            LocalDate.of(1900, 1, 1),
            LocalDate.of(1988, 2, 29),
            LocalDate.of(2000, 1, 1),
            LocalDate.of(2024, 3, 20),
            LocalDate.of(2025, 3, 20),
            LocalDate.of(2036, 2, 29),
            LocalDate.of(2100, 12, 31),
        )

        samples.forEach { gregorian ->
            assertEquals(
                "Round trip failed for $gregorian",
                gregorian,
                PersianDate.fromGregorian(gregorian).toGregorian(),
            )
        }
    }

    @Test
    fun documented_supported_year_boundaries_round_trip() {
        val minimum = PersianDate(PersianCalendarEngine.MIN_SUPPORTED_YEAR, 1, 1)
        val maximumYear = PersianCalendarEngine.MAX_SUPPORTED_YEAR
        val maximum = PersianDate(
            year = maximumYear,
            month = 12,
            day = PersianCalendarEngine.lengthOfMonth(maximumYear, 12),
        )

        assertEquals(minimum, PersianDate.fromGregorian(minimum.toGregorian()))
        assertEquals(maximum, PersianDate.fromGregorian(maximum.toGregorian()))

        assertThrows(IllegalArgumentException::class.java) {
            PersianDate.fromGregorian(minimum.toGregorian().minusDays(1))
        }
        assertThrows(IllegalArgumentException::class.java) {
            PersianDate.fromGregorian(maximum.toGregorian().plusDays(1))
        }
    }

    @Test
    fun every_gregorian_day_from_1900_through_2100_round_trips() {
        var current = LocalDate.of(1900, 1, 1)
        val end = LocalDate.of(2100, 12, 31)

        while (!current.isAfter(end)) {
            assertEquals(
                "Round trip failed for $current",
                current,
                PersianDate.fromGregorian(current).toGregorian(),
            )
            current = current.plusDays(1)
        }
    }

    @Test
    fun month_navigation_keeps_esfand_between_bahman_and_farvardin() {
        val bahman = PersianDate(1404, 11, 15)

        assertEquals(PersianDate(1404, 12, 15), bahman.plusMonths(1))
        assertEquals(PersianDate(1405, 1, 15), bahman.plusMonths(2))
    }
    @Test
    fun every_supported_persian_day_round_trips() {
        for (year in PersianCalendarEngine.MIN_SUPPORTED_YEAR..PersianCalendarEngine.MAX_SUPPORTED_YEAR) {
            for (month in 1..12) {
                val days = PersianCalendarEngine.lengthOfMonth(year, month)
                for (day in 1..days) {
                    val persian = PersianDate(year, month, day)
                    assertEquals(
                        "Round trip failed for $persian",
                        persian,
                        PersianDate.fromGregorian(persian.toGregorian()),
                    )
                }
            }
        }
    }

    @Test
    fun month_arithmetic_rejects_overflow_and_out_of_range_results() {
        val month = PersianYearMonth(1404, 1)

        assertThrows(ArithmeticException::class.java) {
            month.plusMonths(Long.MAX_VALUE)
        }
        assertThrows(ArithmeticException::class.java) {
            month.minusMonths(Long.MIN_VALUE)
        }
        assertThrows(IllegalArgumentException::class.java) {
            month.plusMonths(50_000L)
        }
    }

    @Test
    fun extreme_gregorian_dates_are_rejected_before_integer_conversion_overflow() {
        val formerlyAliasedDate = LocalDate.of(-999_532_778, 7, 1)

        assertFalse(PersianCalendar.isSupported(LocalDate.MIN))
        assertFalse(PersianCalendar.isSupported(LocalDate.MAX))
        assertFalse(PersianCalendar.isSupported(formerlyAliasedDate))
        assertEquals(null, PersianCalendar.tryFromGregorian(formerlyAliasedDate))
        assertThrows(IllegalArgumentException::class.java) {
            PersianCalendar.fromGregorian(formerlyAliasedDate)
        }
    }

    @Test
    fun public_gregorian_boundaries_match_persian_boundaries() {
        val minimum = PersianDate(PersianCalendar.MIN_SUPPORTED_YEAR, 1, 1)
        val maximumYear = PersianCalendar.MAX_SUPPORTED_YEAR
        val maximum = PersianDate(
            maximumYear,
            12,
            PersianCalendar.lengthOfMonth(maximumYear, 12),
        )

        assertEquals(minimum.toGregorian(), PersianCalendar.MIN_SUPPORTED_GREGORIAN_DATE)
        assertEquals(maximum.toGregorian(), PersianCalendar.MAX_SUPPORTED_GREGORIAN_DATE)
    }

}
