package com.msa.coreui.calendar.utilspersion

import android.os.Parcelable

/**
 * Data model of dates, to pass to the Adapter to Create the gridView.
 */
data class CalendarModel(
    val iranianDay: Int,
    val iranianMonth: Int,
    val iranianYear: Int,
    val dayOfWeek: Int,
    var gDay: Int,
    val gMonth: Int,
    val gYear: Int,
    var isHoliday: Boolean = false,
    var today: Boolean = false
)
