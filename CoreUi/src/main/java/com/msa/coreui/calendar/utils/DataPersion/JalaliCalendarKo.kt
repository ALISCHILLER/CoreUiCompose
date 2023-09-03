package com.msa.coreui.calendar.utils.DataPersion

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

class JalaliCalendarko : Calendar {
    companion object {
        val gregorianDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jalaliDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        const val FARVARDIN = 0
        const val ORDIBEHESHT = 1
        const val KHORDAD = 2
        const val TIR = 3
        const val MORDAD = 4
        const val SHAHRIVAR = 5
        const val MEHR = 6
        const val ABAN = 7
        const val AZAR = 8
        const val DEY = 9
        const val BAHMAN = 10
        const val ESFAND = 11

        private var timeZone = TimeZone.getDefault()
        private var isTimeSeted = false

        private const val ONE_SECOND = 1000
        private const val ONE_MINUTE = 60 * ONE_SECOND
        private const val ONE_HOUR = 60 * ONE_MINUTE
        private const val ONE_DAY = 24 * ONE_HOUR
        const val BCE = 0
        const val CE = 1
        const val AD = 1
    }

    private var cal: GregorianCalendar? = null

    init {
        this(TimeZone.getDefault(), Locale.getDefault())
    }

    private operator fun invoke(default: TimeZone?, default1: Locale) {

    }

    constructor(zone: TimeZone) : this(zone, Locale.getDefault())

    constructor(locale: Locale) : this(TimeZone.getDefault(), locale)

    constructor(zone: TimeZone, locale: Locale) : super(zone, locale) {
        timeZone = zone
        val calendar = Calendar.getInstance(zone, locale)

        val yearMonthDate = YearMonthDate(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DATE))
        val convertedDate = gregorianToJalali(yearMonthDate)
        set(convertedDate.year, convertedDate.month, convertedDate.date)
        complete()
    }

    constructor(year: Int, month: Int, dayOfMonth: Int) : this(year, month, dayOfMonth, 0, 0, 0, 0)

    constructor(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int
    ) : this(year, month, dayOfMonth, hourOfDay, minute, 0, 0)

    constructor(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int,
        second: Int
    ) : this(year, month, dayOfMonth, hourOfDay, minute, second, 0)

    private constructor(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int,
        second: Int,
        millis: Int
    ) : super() {
        set(YEAR, year)
        set(MONTH, month)
        set(DAY_OF_MONTH, dayOfMonth)

        if (hourOfDay >= 12 && hourOfDay <= 23) {
            set(AM_PM, PM)
            set(HOUR, hourOfDay - 12)
        } else {
            set(HOUR, hourOfDay)
            set(AM_PM, AM)
        }

        set(HOUR_OF_DAY, hourOfDay)
        set(MINUTE, minute)
        set(SECOND, second)
        set(MILLISECOND, millis)

        val yearMonthDate = jalaliToGregorian(YearMonthDate(fields[1], fields[2], fields[5]))
        cal = GregorianCalendar(
            yearMonthDate.year,
            yearMonthDate.month,
            yearMonthDate.date,
            hourOfDay,
            minute,
            second
        )
        time = cal!!.timeInMillis

        isTimeSeted = true
    }

    override fun computeTime() {
        if (!isTimeSet && !isTimeSeted) {
            val cal = GregorianCalendar.getInstance(timeZone)
            if (!isSet(HOUR_OF_DAY)) {
                super.set(HOUR_OF_DAY, cal.get(HOUR_OF_DAY))
            }
            if (!isSet(HOUR)) {
                super.set(HOUR, cal.get(HOUR))
            }
            if (!isSet(MINUTE)) {
                super.set(MINUTE, cal.get(MINUTE))
            }
            if (!isSet(SECOND)) {
                super.set(SECOND, cal.get(SECOND))
            }
            if (!isSet(MILLISECOND)) {
                super.set(MILLISECOND, cal.get(MILLISECOND))
            }
            if (!isSet(ZONE_OFFSET)) {
                super.set(ZONE_OFFSET, cal.get(ZONE_OFFSET))
            }
            if (!isSet(DST_OFFSET)) {
                super.set(DST_OFFSET, cal.get(DST_OFFSET))
            }
            if (!isSet(AM_PM)) {
                super.set(AM_PM, cal.get(AM_PM))
            }

            if (internalGet(HOUR_OF_DAY) >= 12 && internalGet(HOUR_OF_DAY) <= 23) {
                super.set(AM_PM, PM)
                super.set(HOUR, internalGet(HOUR_OF_DAY) - 12)
            } else {
                super.set(HOUR, internalGet(HOUR_OF_DAY))
                super.set(AM_PM, AM)
            }
            val yearMonthDate = jalaliToGregorian(YearMonthDate(internalGet(YEAR), internalGet(MONTH), internalGet(DAY_OF_MONTH)))
            cal.set(
                yearMonthDate.year,
                yearMonthDate.month,
                yearMonthDate.date,
                internalGet(HOUR_OF_DAY),
                internalGet(MINUTE),
                internalGet(SECOND)
            )
            time = cal.timeInMillis
        } else if (isTimeSeted) {
            isTimeSeted = false
        }
        if (!isSet(ZONE_OFFSET)) {
            super.set(ZONE_OFFSET, 0)
        }
        if (!isSet(DST_OFFSET)) {
            super.set(DST_OFFSET, 0)
        }
    }

    override fun computeFields() {
        if (!isTimeSeted) {
            val yearMonthDate = gregorianToJalali(YearMonthDate(fields[1], fields[2], fields[5]))
            super.set(YEAR, yearMonthDate.year)
            super.set(MONTH, yearMonthDate.month)
            super.set(DAY_OF_MONTH, yearMonthDate.date)
        } else if (isTimeSeted) {
            isTimeSeted = false
        }
    }

    private fun YearMonthDate.getMonthLength(): Int {
        return if (isLeap()) {
            jalaliDaysInMonth[month] + 1
        } else {
            jalaliDaysInMonth[month]
        }
    }

    private fun YearMonthDate.isLeap(): Boolean {
        return ((year - 474) % 2820 + 512) % 682 % 4 != 0
    }

    private fun jalaliToGregorian(jalali: YearMonthDate): YearMonthDate {
        val gy = jalali.year + 621
        val days = jalali.dayOfYear()
        val lastDay = if (jalali.isLeap()) 366 else 365
        val yearMonthDate = YearMonthDate(gy, 0, 0)
        var day: Int
        day = if (days <= lastDay) {
            days
        } else {
            days - lastDay
        }
        val monthDays = if (jalali.isLeap()) 30 else 29
        for (i in 0 until 12) {
            if (day <= monthDays) {
                yearMonthDate.month = i
                yearMonthDate.date = day
                break
            }
            day -= monthDays
        }
        return yearMonthDate
    }

    private fun gregorianToJalali(gregorian: YearMonthDate): YearMonthDate {
        var jy: Int
        var gy: Int
        var day2: Int
        var d: Int
        val jm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        if (gregorian.year < 1600) {
            gy = gregorian.year - 621
            day2 = gregorian.dayOfYear() + 79
        } else {
            jy = gregorian.year - 1600
            day2 = gregorian.dayOfYear() - 79
            jy = jy / 4 - jy / 100 + (jy + 100) / 400
            gy = 1600 + jy
        }
        for (i in 0..11) {
            if (jm[i] <= day2) {
                d = day2 - jm[i]
                if (i >= 10) {
                    gy++
                }
                if (i >= 1 && d <= 2) {
                    d++
                }
                day2 = d
                break
            }
        }
        return YearMonthDate(gy, day2 - 1, 0)
    }

    override fun roll(field: Int, up: Boolean) {
        complete()
        val value = get(field)
        when (field) {
            AM_PM -> {
                if (up) {
                    val hourOfDay = internalGet(HOUR_OF_DAY)
                    if (hourOfDay >= 0 && hourOfDay < 12) {
                        super.set(HOUR_OF_DAY, hourOfDay + 12)
                    }
                } else {
                    val hourOfDay = internalGet(HOUR_OF_DAY)
                    if (hourOfDay >= 12 && hourOfDay <= 23) {
                        super.set(HOUR_OF_DAY, hourOfDay - 12)
                    }
                }
            }
            else -> {
                if (up) {
                    super.set(field, value + 1)
                } else {
                    super.set(field, value - 1)
                }
            }
        }
    }

    override fun getDisplayName(field: Int, style: Int, locale: Locale): String {
        throw UnsupportedOperationException("JalaliCalendarko does not support this operation")
    }

    override fun add(field: Int, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun getMinimum(field: Int): Int {
        throw UnsupportedOperationException("ko does not support this operation")
    }

    override fun getMaximum(field: Int): Int {
        throw UnsupportedOperationException("JalaliCalendarko does not support this operation")
    }

    override fun getGreatestMinimum(field: Int): Int {
        throw UnsupportedOperationException("JalaliCalendarko does not support this operation")
    }

    override fun getLeastMaximum(field: Int): Int {
        throw UnsupportedOperationException("JalaliCalendarko does not support this operation")
    }

    class YearMonthDate(var year: Int, var month: Int, var date: Int) {
        fun dayOfYear(): Int {
            var day = 0
            for (i in 0 until month) {
                day += jalaliDaysInMonth[i]
            }
            day += date
            return day
        }
    }
}
