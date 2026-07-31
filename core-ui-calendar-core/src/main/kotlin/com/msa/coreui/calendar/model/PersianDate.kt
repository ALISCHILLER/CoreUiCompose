package com.msa.coreui.calendar.model

import com.msa.coreui.calendar.internal.PersianCalendarEngine
import java.io.Serializable
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Immutable Solar Hijri (Jalali) date.
 *
 * Gregorian [LocalDate] remains the recommended transport/storage representation. This value object
 * provides type-safe Persian calendar display, validation and arithmetic without locale parsing.
 */
public data class PersianDate(
    public val year: Int,
    public val month: Int,
    public val day: Int,
) : Comparable<PersianDate>, Serializable {

    init {
        require(year in PersianCalendarEngine.MIN_SUPPORTED_YEAR..PersianCalendarEngine.MAX_SUPPORTED_YEAR) {
            "Persian year must be between ${PersianCalendarEngine.MIN_SUPPORTED_YEAR} and ${PersianCalendarEngine.MAX_SUPPORTED_YEAR}."
        }
        require(month in 1..12) { "Persian month must be between 1 and 12." }
        require(day in 1..PersianCalendarEngine.lengthOfMonth(year, month)) {
            "Invalid Persian day $day for $year/$month."
        }
    }

    public val isLeapYear: Boolean
        get() = PersianCalendarEngine.isLeapYear(year)

    public val lengthOfMonth: Int
        get() = PersianCalendarEngine.lengthOfMonth(year, month)

    public val dayOfWeek: DayOfWeek
        get() = toGregorian().dayOfWeek

    public fun toGregorian(): LocalDate = PersianCalendarEngine.toGregorian(this)

    public fun toEpochDay(): Long = toGregorian().toEpochDay()

    public fun withDayOfMonth(newDay: Int): PersianDate = PersianDate(year, month, newDay)

    public fun plusDays(days: Long): PersianDate = fromGregorian(toGregorian().plusDays(days))

    public fun minusDays(days: Long): PersianDate = plusDays(Math.negateExact(days))

    public fun plusMonths(months: Long): PersianDate {
        val target = PersianYearMonth(year, month).plusMonths(months)
        return PersianDate(target.year, target.month, day.coerceAtMost(target.lengthOfMonth))
    }

    public fun minusMonths(months: Long): PersianDate = plusMonths(Math.negateExact(months))

    public fun plusYears(years: Long): PersianDate {
        val newYear = Math.addExact(year.toLong(), years)
        require(newYear in PersianCalendarEngine.MIN_SUPPORTED_YEAR.toLong()..PersianCalendarEngine.MAX_SUPPORTED_YEAR.toLong()) {
            "Resulting Persian year $newYear is outside the supported range."
        }
        val targetDay = day.coerceAtMost(PersianCalendarEngine.lengthOfMonth(newYear.toInt(), month))
        return PersianDate(newYear.toInt(), month, targetDay)
    }

    public fun minusYears(years: Long): PersianDate = plusYears(Math.negateExact(years))

    public fun daysUntil(other: PersianDate): Long = other.toEpochDay() - toEpochDay()

    public override fun compareTo(other: PersianDate): Int = compareValuesBy(
        this,
        other,
        PersianDate::year,
        PersianDate::month,
        PersianDate::day,
    )

    public override fun toString(): String =
        "${formatYear(year)}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

    public companion object {
        private val ISO_PATTERN: Regex = Regex("^([+-]?\\d{4,})-(\\d{2})-(\\d{2})$")

        @JvmStatic
        public fun fromGregorian(date: LocalDate): PersianDate = PersianCalendarEngine.fromGregorian(date)

        @JvmStatic
        public fun tryFromGregorian(date: LocalDate): PersianDate? = try {
            fromGregorian(date)
        } catch (_: IllegalArgumentException) {
            null
        }

        @JvmStatic
        public fun fromEpochDay(epochDay: Long): PersianDate = fromGregorian(LocalDate.ofEpochDay(epochDay))

        @JvmStatic
        @JvmOverloads
        public fun now(clock: Clock = Clock.systemDefaultZone()): PersianDate = fromGregorian(LocalDate.now(clock))

        @JvmStatic
        public fun parseIso(value: String): PersianDate {
            val match = ISO_PATTERN.matchEntire(value.trim())
                ?: throw IllegalArgumentException("Persian date must use yyyy-MM-dd format.")
            return PersianDate(
                year = match.groupValues[1].toInt(),
                month = match.groupValues[2].toInt(),
                day = match.groupValues[3].toInt(),
            )
        }

        @JvmStatic
        public fun tryParseIso(value: String): PersianDate? = try {
            parseIso(value)
        } catch (_: IllegalArgumentException) {
            null
        }

        private fun formatYear(year: Int): String = if (year >= 0) {
            year.toString().padStart(4, '0')
        } else {
            "-${kotlin.math.abs(year).toString().padStart(4, '0')}"
        }
    }
}

/** A Persian calendar year and month with safe month arithmetic. */
public data class PersianYearMonth(
    public val year: Int,
    public val month: Int,
) : Comparable<PersianYearMonth>, Serializable {

    init {
        require(year in PersianCalendarEngine.MIN_SUPPORTED_YEAR..PersianCalendarEngine.MAX_SUPPORTED_YEAR) {
            "Persian year must be between ${PersianCalendarEngine.MIN_SUPPORTED_YEAR} and ${PersianCalendarEngine.MAX_SUPPORTED_YEAR}."
        }
        require(month in 1..12) { "Persian month must be between 1 and 12." }
    }

    public val lengthOfMonth: Int
        get() = PersianCalendarEngine.lengthOfMonth(year, month)

    public val firstDay: PersianDate
        get() = PersianDate(year, month, 1)

    public val lastDay: PersianDate
        get() = PersianDate(year, month, lengthOfMonth)

    public fun atDay(day: Int): PersianDate = PersianDate(year, month, day)

    public fun plusMonths(months: Long): PersianYearMonth {
        val baseMonth = Math.addExact(
            Math.multiplyExact(year.toLong(), MONTHS_PER_YEAR),
            (month - 1).toLong(),
        )
        val absoluteMonth = Math.addExact(baseMonth, months)
        val newYear = Math.floorDiv(absoluteMonth, MONTHS_PER_YEAR)
        require(newYear in PersianCalendarEngine.MIN_SUPPORTED_YEAR.toLong()..PersianCalendarEngine.MAX_SUPPORTED_YEAR.toLong()) {
            "Resulting Persian year $newYear is outside the supported range."
        }
        val newMonth = Math.floorMod(absoluteMonth, MONTHS_PER_YEAR).toInt() + 1
        return PersianYearMonth(newYear.toInt(), newMonth)
    }

    public fun minusMonths(months: Long): PersianYearMonth = plusMonths(Math.negateExact(months))

    public override fun compareTo(other: PersianYearMonth): Int = when {
        year != other.year -> year.compareTo(other.year)
        else -> month.compareTo(other.month)
    }

    public override fun toString(): String =
        "${if (year >= 0) year.toString().padStart(4, '0') else "-${kotlin.math.abs(year).toString().padStart(4, '0')}"}-${month.toString().padStart(2, '0')}"

    public companion object {
        private const val MONTHS_PER_YEAR: Long = 12L
        private val ISO_PATTERN: Regex = Regex("^([+-]?\\d{4,})-(\\d{2})$")

        @JvmStatic
        public fun fromGregorian(date: LocalDate): PersianYearMonth {
            val persian = PersianDate.fromGregorian(date)
            return PersianYearMonth(persian.year, persian.month)
        }

        @JvmStatic
        public fun parseIso(value: String): PersianYearMonth {
            val match = ISO_PATTERN.matchEntire(value.trim())
                ?: throw IllegalArgumentException("Persian year-month must use yyyy-MM format.")
            return PersianYearMonth(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        }

        @JvmStatic
        public fun tryParseIso(value: String): PersianYearMonth? = try {
            parseIso(value)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
