/*
 *  Copyright (C) 2022-2023. Maximilian Keppeler (https://www.maxkeppeler.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.msa.coreui.calendar.models

import com.msa.coreui.calendar.utils.DataPersion.PersianMonth

/**
 * Defines information for the months selection.
 * @param disabled the months that are displayed.
 * @param selected the month that is selected.
 * @param thisMonth the current month.
 */
internal data class CalendarMonthData(
    val selected: PersianMonth,
    val disabled: List<PersianMonth>,
    val thisMonth: PersianMonth,
)