package com.msa.coreui.calendar.utils.DataPersion

import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class PersionCalendar {
   private  var year: Int = 0
   private  var month: Int = 0
    private var day: Int = 0

    /**
     * Today Jalali Date
     */
    constructor() {
        fromGregorian(GregorianCalendar())
    }

    /**
     * Create a ir.huri.jcal.JalaliCalendar object
     * @param year Jalali Year
     * @param month Jalali Month
     * @param day Jalali Day
     */
    constructor(year: Int, month: Int, day: Int) {
        set(year, month, day)
    }

    /**
     * Create a ir.huri.jcal.JalaliCalendar object from gregorian calendar
     * @param gc gregorian calendar object
     */
    constructor(gc: GregorianCalendar) {
        fromGregorian(gc)
    }

    /**
     * Create a ir.huri.jcal.JalaliCalendar object from Localdate(java 8)
     * @param ld local date object
     */
    constructor(ld: LocalDate) {
        fromGregorian(GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault())))
    }

    /**
     * Create a ir.huri.jcal.JalaliCalendar object from Date object
     * @param date Date object
     */
    constructor(date: Date) {
        val gc = GregorianCalendar()
        gc.time = date
        fromGregorian(gc)
    }

    /**
     * Convert current jalali date to gregorian date
     * @return date converted gregorianDate
     */
    fun toGregorian(): GregorianCalendar {
        val julianDay = toJulianDay()
        return julianDayToGregorianCalendar(julianDay)
    }

    /**
     * set date from gregorian date
     * @param gc input gregorian calendar
     */
    fun fromGregorian(gc: GregorianCalendar) {
        val jd = gregorianToJulianDayNumber(gc)
        fromJulianDay(jd)
    }

    /**
     * @return yesterday date
     */
    fun getYesterday(): PersionCalendar {
        return getDateByDiff(-1)
    }

    /**
     * @return tomorrow date
     */
    fun getTomorrow(): PersionCalendar {
        return getDateByDiff(1)
    }

    /**
     * get Jalali date by day difference
     * @param diff number of day diffrents
     * @return jalali calendar diffحزن
     */
    fun getDateByDiff(diff: Int): PersionCalendar {
        val gc = toGregorian()
        gc.add(Calendar.DAY_OF_MONTH, diff)
        return PersionCalendar(gc)
    }

    /**
     * @return day Of Week
     */
    fun getDayOfWeek(): Int {
        return toGregorian().get(Calendar.DAY_OF_WEEK)
    }

    /**
     * @return get first day of week
     */
    fun getFirstDayOfWeek(): Int {
        return toGregorian().firstDayOfWeek
    }
    fun dayOfWeek(): Int {
        return toGregorian().get(Calendar.DAY_OF_WEEK)
    }
    /**
     * @return day name
     */
    fun getDayOfWeekString(): String {
        when (getDayOfWeek()) {
            1 -> return "یک‌شنبه"
            2 -> return "دوشنبه"
            3 -> return "سه‌شنبه"
            4 -> return "چهارشنبه"
            5 -> return "پنجشنبه"
            6 -> return "جمعه"
            7 -> return "شنبه"
            else -> return "نامعلوم"
        }
    }

    /**
     * @return month name
     */
    fun getMonthString(): String {
        when (getMonth()) {
            1 -> return "فروردین"
            2 -> return "اردیبهشت"
            3 -> return "خرداد"
            4 -> return "تیر"
            5 -> return "مرداد"
            6 -> return "شهریور"
            7 -> return "مهر"
            8 -> return "آبان"
            9 -> return "آذر"
            10 -> return "دی"
            11 -> return "بهمن"
            12 -> return "اسفند"
            else -> return "نامعلوم"
        }
    }

    /**
     * get String with the following format :
     *  یکشنبه ۱۲ آبان
     * @return String format
     */
    fun getDayOfWeekDayMonthString(): String {
        return getDayOfWeekString() + " " + getDay() + " " + getMonthString()
    }

    /**
     * @return return whether this year is a jalali leap year
     */
    fun isLeap(): Boolean {
        return getLeapFactor(getYear()) == 0
    }

    fun getYearLength(): Int {
        return if (isLeap()) 366 else 365
    }

    fun getMonthLength(): Int {
        return if (getMonth() < 7) {
            31
        } else if (getMonth() < 12) {
            30
        } else if (getMonth() == 12) {
            if (isLeap()) 30 else 29
        } else {
            0
        }
    }

    fun getDay(): Int {
        return day
    }

    fun getMonth(): Int {
        return month
    }

    fun getYear(): Int {
        return year
    }

    fun setMonth(month: Int) {
        if (month in 1..12) {
            this.month = month
        } else {
            throw IllegalArgumentException("Month should be between 1 and 12 $month .")
        }
    }

    fun setYear(year: Int) {
        if (year >= 0) {
            this.year = year
        } else {
            throw IllegalArgumentException("Year should be a non-negative integer.")
        }
    }

    fun setDay(day: Int) {
        if (day in 1..31) { // تغییر این بخش به مطابق با تعداد روزهای معتبر در ماه‌های جلالی
            this.day = day
        } else {
            throw IllegalArgumentException("Day should be between 1 and 31.") // تغییر پیام خطا به مطابق با ماه‌های جلالی
        }
    }

    fun set(year: Int, month: Int, day: Int) {
        setYear(year)
        setMonth(month)
        setDay(day)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as PersionCalendar

        return year == that.year && month == that.month && day == that.day
    }

    private fun gregorianToJulianDayNumber(gc: GregorianCalendar): Int {
        val gregorianYear = gc.get(Calendar.YEAR)
        val gregorianMonth = gc.get(Calendar.MONTH) + 1
        val gregorianDay = gc.get(Calendar.DAY_OF_MONTH)

        return (((1461 * (gregorianYear + 4800 + (gregorianMonth - 14) / 12)) / 4
                + (367 * (gregorianMonth - 2 - 12 * ((gregorianMonth - 14) / 12))) / 12
                - (3 * ((gregorianYear + 4900 + (gregorianMonth - 14) / 12) / 100)) / 4 + gregorianDay
                - 32075) - (gregorianYear + 100100 + (gregorianMonth - 8) / 6) / 100 * 3 / 4 + 752)
    }

    private fun julianToJulianDayNumber(jc: JulianCalendar): Int {
        val julianYear = jc.year
        val julianMonth = jc.month
        val JulianDay = jc.day

        return (1461 * (julianYear + 4800 + (julianMonth - 14) / 12)) / 4
        + (367 * (julianMonth - 2 - 12 * ((julianMonth - 14) / 12))) / 12
        - (3 * ((julianYear + 4900 + (julianMonth - 14) / 12) / 100)) / 4 + JulianDay
        - 32075
    }

    private fun julianDayToGregorianCalendar(JulianDayNumber: Int): GregorianCalendar {
        val j = 4 * JulianDayNumber + 139361631 + (4 * JulianDayNumber + 183187720) / 146097 * 3 / 4 * 4 - 3908
        val i = (j % 1461) / 4 * 5 + 308

        val gregorianDay = (i % 153) / 5 + 1
        val gregorianMonth = ((i / 153) % 12) + 1
        val gregorianYear = j / 1461 - 100100 + (8 - gregorianMonth) / 6

        return GregorianCalendar(gregorianYear, gregorianMonth - 1, gregorianDay)
    }

    private fun fromJulianDay(JulianDayNumber: Int) {
        val gc = julianDayToGregorianCalendar(JulianDayNumber)
        val gregorianYear = gc.get(Calendar.YEAR)

        var jalaliYear: Int
        var jalaliMonth: Int
        var jalaliDay: Int

        jalaliYear = gregorianYear - 621

        val gregorianFirstFarvardin = getGregorianFirstFarvardin()
        val JulianDayFarvardinFirst = gregorianToJulianDayNumber(gregorianFirstFarvardin)
        var diffFromFarvardinFirst = JulianDayNumber - JulianDayFarvardinFirst

        if (diffFromFarvardinFirst >= 0) {
            if (diffFromFarvardinFirst <= 185) {
                jalaliMonth = 1 + diffFromFarvardinFirst / 31
                jalaliDay = (diffFromFarvardinFirst % 31) + 1
                set(jalaliYear, jalaliMonth, jalaliDay)
                return
            } else {
                diffFromFarvardinFirst = diffFromFarvardinFirst - 186
            }
        } else {
            diffFromFarvardinFirst = diffFromFarvardinFirst + 179
            if (getLeapFactor(jalaliYear) == 1)
                diffFromFarvardinFirst = diffFromFarvardinFirst + 1
            jalaliYear -= 1
        }

        jalaliMonth = 7 + diffFromFarvardinFirst / 30
        jalaliDay = (diffFromFarvardinFirst % 30) + 1
        set(jalaliYear, jalaliMonth, jalaliDay)
    }

    private fun toJulianDay(): Int {
        val jalaliMonth = getMonth()
        val jalaliDay = getDay()

        val gregorianFirstFarvardin = getGregorianFirstFarvardin()
        val gregorianYear = gregorianFirstFarvardin.get(Calendar.YEAR)
        val gregorianMonth = gregorianFirstFarvardin.get(Calendar.MONTH) + 1
        val gregorianDay = gregorianFirstFarvardin.get(Calendar.DAY_OF_MONTH)

        val julianFirstFarvardin = JulianCalendar(gregorianYear, gregorianMonth, gregorianDay)

        var julianDay = julianToJulianDayNumber(julianFirstFarvardin) + (jalaliMonth - 1) * 31 - jalaliMonth / 7 * (jalaliMonth - 7)
        + jalaliDay - 1

        return julianDay
    }

    private fun getGregorianFirstFarvardin(): GregorianCalendar {
        var marchDay = 0
        val breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
            1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)

        val jalaliYear = getYear()
        val gregorianYear = jalaliYear + 621
        var jalaliLeap = -14
        var jp = breaks[0]

        var jump = 0
        for (j in 1..19) {
            val jm = breaks[j]
            jump = jm - jp
            if (jalaliYear < jm) {
                var N = jalaliYear - jp
                jalaliLeap = jalaliLeap + N / 33 * 8 + (N % 33 + 3) / 4

                if (jump % 33 == 4 && jump - N == 4)
                    jalaliLeap = jalaliLeap + 1

                val GregorianLeap = (gregorianYear / 4) - (gregorianYear / 100 + 1) * 3 / 4 - 150

                marchDay = 20 + (jalaliLeap - GregorianLeap)

                if (jump - N < 6)
                    N = N - jump + (jump + 4) / 33 * 33

                break
            }

            jalaliLeap = jalaliLeap + jump / 33 * 8 + (jump % 33) / 4
            jp = jm
        }

        return GregorianCalendar(gregorianYear, 2, marchDay)
    }

    private fun getLeapFactor(jalaliYear: Int): Int {
        var leap = 0
        val breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
            1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)

        var jp = breaks[0]

        var jump = 0
        for (j in 1..19) {
            val jm = breaks[j]
            jump = jm - jp
            if (jalaliYear < jm) {
                var N = jalaliYear - jp

                if (jump - N < 6)
                    N = N - jump + (jump + 4) / 33 * 33

                leap = ((((N + 1) % 33) - 1) % 4)

                if (leap == -1)
                    leap = 4

                break
            }

            jp = jm
        }

        return leap
    }

    override fun toString(): String {
        return String.format("%04d-%02d-%02d", getYear(), getMonth(), getDay())
    }

    private inner class JulianCalendar internal constructor(year: Int, month: Int, day: Int) {
        val year: Int = year
        val month: Int = month
        val day: Int = day
    }
}
