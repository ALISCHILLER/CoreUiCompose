package com.msa.coreui.calendar.utils

import java.time.LocalDate
import java.time.chrono.Chronology
import java.time.chrono.HijrahChronology
import java.time.chrono.IsoChronology
import java.time.temporal.TemporalAccessor
import java.util.Locale

class PersianDateConverter {
    companion object {
        fun gregorianToPersian(gregorianDate: LocalDate): LocalDate {
            val persianChrono: Chronology = HijrahChronology.INSTANCE
            val temporalAccessor = gregorianDate
            val persianDate = LocalDate.from(persianChrono.date(temporalAccessor))
            return persianDate
        }
    }
}