package com.msa.coreui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.msa.coreui.calendar.model.PersianDate
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.calendar.model.PersianDateSelectionMode
import com.msa.coreui.calendar.model.PersianYearMonth
import com.msa.coreui.calendar.model.isEmpty
import java.time.Clock
import java.time.DateTimeException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.TreeSet

/** The visible section of the date picker. */
enum class PersianDatePickerView {
    DAYS,
    MONTHS,
    YEARS,
}

/** Explains why a date can or cannot be selected by the current picker state. */
enum class PersianDateAvailability {
    SELECTABLE,
    OUT_OF_BOUNDS,
    DISABLED_DATE,
    DISABLED_RANGE,
    DISABLED_WEEKDAY,
    MULTIPLE_SELECTION_LIMIT,
}

/**
 * Hoisted state for [PersianDatePicker].
 *
 * The public selection is expressed as Gregorian [LocalDate] values so it can be safely persisted,
 * sent to APIs and compared without locale-dependent parsing. Persian calendar arithmetic is kept
 * inside the component through [PersianDate] and [PersianYearMonth].
 */
@Stable
class PersianDatePickerState(
    val selectionMode: PersianDateSelectionMode,
    initialSelection: PersianDateSelection,
    initialDisplayedMonth: PersianYearMonth,
    val config: PersianDatePickerConfig,
) {
    init {
        require(initialSelection.mode == selectionMode) {
            "initialSelection mode ${initialSelection.mode} does not match $selectionMode."
        }
    }

    private val minPersianDate = PersianDate.fromGregorian(config.minDate)
    private val maxPersianDate = PersianDate.fromGregorian(config.maxDate)
    private val disabledDates = TreeSet(config.disabledDates)
    private val monthSelectabilityCache = HashMap<PersianYearMonth, Boolean>()
    private val yearSelectabilityCache = HashMap<Int, Boolean>()
    private val firstAllowedMonth = PersianYearMonth(minPersianDate.year, minPersianDate.month)
    private val lastAllowedMonth = PersianYearMonth(maxPersianDate.year, maxPersianDate.month)
    private val allDatesDefinitelyDisabled =
        config.disabledWeekdays.size == DAYS_PER_WEEK.toInt() || isEntireBoundaryExplicitlyDisabled()

    var selection: PersianDateSelection by mutableStateOf(
        sanitizeSelection(initialSelection),
    )
        private set

    var displayedMonth: PersianYearMonth by mutableStateOf(
        initialDisplayedMonth.coerceIn(firstAllowedMonth, lastAllowedMonth),
    )
        private set

    var view: PersianDatePickerView by mutableStateOf(PersianDatePickerView.DAYS)
        private set

    var lastSelectionRejection: PersianDateSelectionRejection? by mutableStateOf(null)
        private set

    init {
        if (
            config.skipFullyDisabledMonths &&
            !allDatesDefinitelyDisabled &&
            !isMonthSelectable(displayedMonth.year, displayedMonth.month)
        ) {
            displayedMonth = nearestSelectableMonth(displayedMonth) ?: displayedMonth
        }
    }

    val availableYears: IntRange
        get() = minPersianDate.year..maxPersianDate.year

    val hasSelection: Boolean
        get() = !selection.isEmpty

    /** True when the configured boundary contains at least one selectable date. */
    val hasSelectableDates: Boolean
        get() = containsSelectableDates

    private val containsSelectableDates: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        !allDatesDefinitelyDisabled && availableYears.any(::isYearSelectable)
    }

    /** Stable reason describing whether the current selection can be confirmed. */
    val confirmationStatus: PersianDateConfirmationStatus
        get() {
            if (selection.isEmpty) {
                return when {
                    config.allowEmptySelectionConfirmation ->
                        PersianDateConfirmationStatus.EMPTY_SELECTION_ALLOWED
                    !hasSelectableDates -> PersianDateConfirmationStatus.NO_SELECTABLE_DATES
                    else -> PersianDateConfirmationStatus.EMPTY_SELECTION_REQUIRED
                }
            }
            return when (val current = selection) {
                is PersianDateSelection.Single -> if (current.date != null) {
                    PersianDateConfirmationStatus.READY
                } else {
                    PersianDateConfirmationStatus.EMPTY_SELECTION_REQUIRED
                }

                is PersianDateSelection.Range -> {
                    val start = current.start
                    val end = current.end
                    if (start == null) {
                        PersianDateConfirmationStatus.EMPTY_SELECTION_REQUIRED
                    } else if (end == null) {
                        PersianDateConfirmationStatus.RANGE_END_REQUIRED
                    } else {
                        PersianDateConfirmationStatus.READY
                    }
                }

                is PersianDateSelection.Multiple -> if (current.dates.size >= config.minMultipleSelection) {
                    PersianDateConfirmationStatus.READY
                } else {
                    PersianDateConfirmationStatus.MULTIPLE_SELECTION_BELOW_MINIMUM
                }
            }
        }

    val canConfirm: Boolean
        get() = confirmationStatus == PersianDateConfirmationStatus.READY ||
            confirmationStatus == PersianDateConfirmationStatus.EMPTY_SELECTION_ALLOWED

    val canNavigatePrevious: Boolean
        get() = if (config.skipFullyDisabledMonths) {
            !allDatesDefinitelyDisabled &&
                findSelectableMonth(displayedMonth, direction = -1, includeStart = false) != null
        } else {
            displayedMonth > firstAllowedMonth
        }

    val canNavigateNext: Boolean
        get() = if (config.skipFullyDisabledMonths) {
            !allDatesDefinitelyDisabled &&
                findSelectableMonth(displayedMonth, direction = 1, includeStart = false) != null
        } else {
            displayedMonth < lastAllowedMonth
        }

    fun showDays() {
        view = PersianDatePickerView.DAYS
    }

    fun showMonths() {
        view = if (view == PersianDatePickerView.MONTHS) {
            PersianDatePickerView.DAYS
        } else {
            PersianDatePickerView.MONTHS
        }
    }

    fun showYears() {
        view = if (view == PersianDatePickerView.YEARS) {
            PersianDatePickerView.DAYS
        } else {
            PersianDatePickerView.YEARS
        }
    }

    fun navigateMonths(delta: Int) {
        tryNavigateMonths(delta)
    }

    /** Navigates by [delta] months and returns whether the displayed month changed. */
    fun tryNavigateMonths(delta: Int): Boolean {
        if (delta == 0) return false
        if (delta < 0 && !canNavigatePrevious) return false
        if (delta > 0 && !canNavigateNext) return false

        var target = displayedMonth.plusMonths(delta.toLong())
            .coerceIn(firstAllowedMonth, lastAllowedMonth)
        if (config.skipFullyDisabledMonths && !isMonthSelectable(target.year, target.month)) {
            val direction = if (delta > 0) 1 else -1
            target = findSelectableMonth(
                start = target,
                direction = direction,
                includeStart = true,
            ) ?: findSelectableMonth(
                start = target,
                direction = -direction,
                includeStart = false,
            )?.takeIf { fallback ->
                if (direction > 0) fallback > displayedMonth else fallback < displayedMonth
            } ?: return false
        }
        if (target == displayedMonth) return false
        displayedMonth = target
        view = PersianDatePickerView.DAYS
        clearSelectionFeedback()
        return true
    }

    fun isMonthAvailable(month: Int): Boolean {
        if (month !in 1..12) return false
        val candidate = PersianYearMonth(displayedMonth.year, month)
        return candidate in firstAllowedMonth..lastAllowedMonth
    }

    /** Returns true when at least one date in the Persian month is selectable. */
    fun isMonthSelectable(year: Int, month: Int): Boolean {
        if (allDatesDefinitelyDisabled) return false
        if (year !in availableYears || month !in 1..12) return false
        val candidate = PersianYearMonth(year, month)
        if (candidate !in firstAllowedMonth..lastAllowedMonth) return false
        return monthSelectabilityCache.getOrPut(candidate) {
            (1..candidate.lengthOfMonth).any { day ->
                baseDateAvailability(candidate.atDay(day).toGregorian()) == PersianDateAvailability.SELECTABLE
            }
        }
    }

    /** Returns true when at least one month in the Persian year contains a selectable date. */
    fun isYearSelectable(year: Int): Boolean {
        if (allDatesDefinitelyDisabled) return false
        if (year !in availableYears) return false
        return yearSelectabilityCache.getOrPut(year) {
            (1..12).any { month -> isMonthSelectable(year, month) }
        }
    }

    fun selectMonth(month: Int) {
        require(month in 1..12) { "Persian month must be between 1 and 12." }
        if (!isMonthAvailable(month)) return
        if (config.skipFullyDisabledMonths && !isMonthSelectable(displayedMonth.year, month)) return
        displayedMonth = PersianYearMonth(displayedMonth.year, month)
        view = PersianDatePickerView.DAYS
        clearSelectionFeedback()
    }

    fun selectYear(year: Int) {
        require(year in availableYears) { "Year $year is outside the configured boundary." }
        if (config.skipFullyDisabledMonths && !isYearSelectable(year)) return
        val requested = PersianYearMonth(year, displayedMonth.month)
            .coerceIn(firstAllowedMonth, lastAllowedMonth)
        displayedMonth = if (!config.skipFullyDisabledMonths || isMonthSelectable(requested.year, requested.month)) {
            requested
        } else {
            nearestSelectableMonthInYear(year, requested.month) ?: requested
        }
        view = PersianDatePickerView.MONTHS
        clearSelectionFeedback()
    }

    fun jumpTo(date: LocalDate) {
        val boundedDate = date.coerceIn(config.minDate, config.maxDate)
        val requested = PersianYearMonth.fromGregorian(boundedDate)
        displayedMonth = if (!config.skipFullyDisabledMonths || isMonthSelectable(requested.year, requested.month)) {
            requested
        } else {
            nearestSelectableMonth(requested) ?: requested
        }
        view = PersianDatePickerView.DAYS
        clearSelectionFeedback()
    }

    /** Navigates to the first selected date when one exists. */
    fun jumpToSelection(): Boolean {
        val date = when (val current = selection) {
            is PersianDateSelection.Single -> current.date
            is PersianDateSelection.Range -> current.start ?: current.end
            is PersianDateSelection.Multiple -> current.dates.minOrNull()
        } ?: return false
        jumpTo(date)
        return true
    }

    /**
     * Finds the closest selectable date inside the configured boundary.
     *
     * Fully disabled months are skipped as units instead of scanning every date across a large
     * boundary. This keeps field initialization and "today" recovery responsive even when long
     * disabled ranges are configured.
     */
    fun nearestSelectableDate(date: LocalDate): LocalDate? {
        if (allDatesDefinitelyDisabled) return null
        val origin = date.coerceIn(config.minDate, config.maxDate)
        if (isDateEnabled(origin)) return origin

        val originMonth = PersianYearMonth.fromGregorian(origin)
        val candidates = buildList {
            nearestSelectableDateInMonth(originMonth, origin)?.let(::add)
            findSelectableMonth(originMonth, direction = -1, includeStart = false)
                ?.let(::lastSelectableDateInMonth)
                ?.let(::add)
            findSelectableMonth(originMonth, direction = 1, includeStart = false)
                ?.let(::firstSelectableDateInMonth)
                ?.let(::add)
        }
        return candidates.minWithOrNull(
            compareBy<LocalDate> { candidate -> kotlin.math.abs(ChronoUnit.DAYS.between(origin, candidate)) }
                .thenBy { candidate -> candidate },
        )
    }

    fun jumpToToday(clock: Clock = Clock.systemDefaultZone()) {
        jumpTo(LocalDate.now(clock))
    }

    /** Returns whether the date passes boundary and disabled-date rules. */
    fun isDateEnabled(date: LocalDate): Boolean =
        baseDateAvailability(date) == PersianDateAvailability.SELECTABLE

    /** Returns whether the date can be interacted with in the current selection state. */
    fun canSelectDate(date: LocalDate): Boolean =
        dateAvailability(date) == PersianDateAvailability.SELECTABLE

    /** Returns the current availability reason without mutating selection. */
    fun dateAvailability(date: LocalDate): PersianDateAvailability {
        val baseAvailability = baseDateAvailability(date)
        if (baseAvailability != PersianDateAvailability.SELECTABLE) return baseAvailability

        val current = selection as? PersianDateSelection.Multiple
            ?: return PersianDateAvailability.SELECTABLE
        if (date in current.dates) return PersianDateAvailability.SELECTABLE
        val limit = config.maxMultipleSelection ?: return PersianDateAvailability.SELECTABLE
        return if (current.dates.size >= limit) {
            PersianDateAvailability.MULTIPLE_SELECTION_LIMIT
        } else {
            PersianDateAvailability.SELECTABLE
        }
    }

    fun selectDate(date: LocalDate) {
        selectDateResult(date)
    }

    /** Applies a selection interaction and returns whether the selection changed. */
    fun trySelectDate(date: LocalDate): Boolean =
        selectDateResult(date) is PersianDateSelectionResult.Changed

    /** Applies a selection interaction and returns a typed success or rejection result. */
    fun selectDateResult(date: LocalDate): PersianDateSelectionResult {
        val availability = dateAvailability(date)
        if (availability != PersianDateAvailability.SELECTABLE) {
            return reject(date, availability.toRejection())
        }
        val previous = selection
        val updated = when (val current = selection) {
            is PersianDateSelection.Single -> PersianDateSelection.Single(date)

            is PersianDateSelection.Multiple -> {
                val dates = current.dates.toMutableSet()
                if (!dates.add(date)) dates.remove(date)
                PersianDateSelection.Multiple(dates.toSortedSet())
            }

            is PersianDateSelection.Range -> {
                val start = current.start
                if (start == null || current.end != null) {
                    PersianDateSelection.Range(date, null)
                } else {
                    val rangeStart = minOf(start, date)
                    val rangeEnd = maxOf(start, date)
                    val rangeDays = rangeLengthDays(rangeStart, rangeEnd)
                    if (rangeDays < config.minRangeDays) {
                        return reject(date, PersianDateSelectionRejection.RANGE_TOO_SHORT)
                    }
                    if (config.maxRangeDays?.let { rangeDays > it } == true) {
                        return reject(date, PersianDateSelectionRejection.RANGE_TOO_LONG)
                    }
                    if (!config.allowDisabledDatesInsideRange && hasDisabledDateBetween(rangeStart, rangeEnd)) {
                        return reject(date, PersianDateSelectionRejection.RANGE_CONTAINS_DISABLED_DATE)
                    }
                    PersianDateSelection.Range(rangeStart, rangeEnd)
                }
            }
        }
        if (updated == previous) {
            clearSelectionFeedback()
            return PersianDateSelectionResult.Unchanged
        }
        selection = updated
        clearSelectionFeedback()
        return PersianDateSelectionResult.Changed(date = date, selection = updated)
    }

    /** Replaces the current selection after applying mode, boundary and disabled-date rules. */
    fun replaceSelection(newSelection: PersianDateSelection) {
        require(newSelection.mode == selectionMode) {
            "Selection mode ${newSelection.mode} does not match $selectionMode."
        }
        selection = sanitizeSelection(newSelection)
        clearSelectionFeedback()
    }

    fun clearSelection() {
        selection = PersianDateSelection.empty(selectionMode)
        clearSelectionFeedback()
    }

    fun clearSelectionFeedback() {
        lastSelectionRejection = null
    }

    fun isSelected(date: LocalDate): Boolean = when (val current = selection) {
        is PersianDateSelection.Single -> current.date == date
        is PersianDateSelection.Range -> current.start == date || current.end == date
        is PersianDateSelection.Multiple -> date in current.dates
    }

    fun isRangeStart(date: LocalDate): Boolean =
        (selection as? PersianDateSelection.Range)?.start == date

    fun isRangeEnd(date: LocalDate): Boolean =
        (selection as? PersianDateSelection.Range)?.end == date

    fun isInsideRange(date: LocalDate): Boolean {
        val range = selection as? PersianDateSelection.Range ?: return false
        val start = range.start ?: return false
        val end = range.end ?: return false
        return date > start && date < end
    }


    private fun baseDateAvailability(date: LocalDate): PersianDateAvailability {
        if (date !in config.minDate..config.maxDate) {
            return PersianDateAvailability.OUT_OF_BOUNDS
        }
        if (date in disabledDates) {
            return PersianDateAvailability.DISABLED_DATE
        }
        if (isInsideDisabledRange(date)) {
            return PersianDateAvailability.DISABLED_RANGE
        }
        if (date.dayOfWeek in config.disabledWeekdays) {
            return PersianDateAvailability.DISABLED_WEEKDAY
        }
        return PersianDateAvailability.SELECTABLE
    }

    private fun reject(
        date: LocalDate,
        reason: PersianDateSelectionRejection,
    ): PersianDateSelectionResult.Rejected {
        lastSelectionRejection = reason
        return PersianDateSelectionResult.Rejected(date = date, reason = reason)
    }

    private fun PersianDateAvailability.toRejection(): PersianDateSelectionRejection = when (this) {
        PersianDateAvailability.OUT_OF_BOUNDS -> PersianDateSelectionRejection.OUT_OF_BOUNDS
        PersianDateAvailability.DISABLED_DATE -> PersianDateSelectionRejection.DISABLED_DATE
        PersianDateAvailability.DISABLED_RANGE -> PersianDateSelectionRejection.DISABLED_RANGE
        PersianDateAvailability.DISABLED_WEEKDAY -> PersianDateSelectionRejection.DISABLED_WEEKDAY
        PersianDateAvailability.MULTIPLE_SELECTION_LIMIT -> PersianDateSelectionRejection.MULTIPLE_SELECTION_LIMIT
        PersianDateAvailability.SELECTABLE -> error("Selectable dates do not have a rejection reason.")
    }

    private fun hasDisabledDateBetween(start: LocalDate, end: LocalDate): Boolean {
        if (!start.isBefore(end.minusDays(1L))) return false
        val interiorStart = start.plusDays(1L)
        val interiorEnd = end.minusDays(1L)

        if (disabledDates.ceiling(interiorStart)?.let { it <= interiorEnd } == true) {
            return true
        }
        if (hasDisabledRangeOverlap(interiorStart, interiorEnd)) return true
        if (config.disabledWeekdays.isEmpty()) return false

        val interiorDayCount = ChronoUnit.DAYS.between(interiorStart, interiorEnd) + 1L
        if (interiorDayCount >= DAYS_PER_WEEK) return true

        var current = interiorStart
        while (!current.isAfter(interiorEnd)) {
            if (current.dayOfWeek in config.disabledWeekdays) return true
            current = current.plusDays(1L)
        }
        return false
    }

    private fun findSelectableMonth(
        start: PersianYearMonth,
        direction: Int,
        includeStart: Boolean,
    ): PersianYearMonth? {
        require(direction == -1 || direction == 1) { "direction must be -1 or 1." }
        var candidate = if (includeStart) start else {
            if (direction > 0) {
                if (start >= lastAllowedMonth) return null
                start.plusMonths(1L)
            } else {
                if (start <= firstAllowedMonth) return null
                start.minusMonths(1L)
            }
        }
        while (candidate in firstAllowedMonth..lastAllowedMonth) {
            if (isMonthSelectable(candidate.year, candidate.month)) return candidate
            if (direction > 0) {
                if (candidate >= lastAllowedMonth) break
                candidate = candidate.plusMonths(1L)
            } else {
                if (candidate <= firstAllowedMonth) break
                candidate = candidate.minusMonths(1L)
            }
        }
        return null
    }

    private fun nearestSelectableMonth(requested: PersianYearMonth): PersianYearMonth? {
        if (isMonthSelectable(requested.year, requested.month)) return requested
        var previous = requested
        var next = requested
        while (previous > firstAllowedMonth || next < lastAllowedMonth) {
            if (previous > firstAllowedMonth) {
                previous = previous.minusMonths(1L)
                if (isMonthSelectable(previous.year, previous.month)) return previous
            }
            if (next < lastAllowedMonth) {
                next = next.plusMonths(1L)
                if (isMonthSelectable(next.year, next.month)) return next
            }
        }
        return null
    }


    private fun nearestSelectableDateInMonth(
        month: PersianYearMonth,
        origin: LocalDate,
    ): LocalDate? = (1..month.lengthOfMonth)
        .asSequence()
        .map { day -> month.atDay(day).toGregorian() }
        .filter(::isDateEnabled)
        .minWithOrNull(
            compareBy<LocalDate> { candidate -> kotlin.math.abs(ChronoUnit.DAYS.between(origin, candidate)) }
                .thenBy { candidate -> candidate },
        )

    private fun firstSelectableDateInMonth(month: PersianYearMonth): LocalDate? =
        (1..month.lengthOfMonth)
            .asSequence()
            .map { day -> month.atDay(day).toGregorian() }
            .firstOrNull(::isDateEnabled)

    private fun lastSelectableDateInMonth(month: PersianYearMonth): LocalDate? =
        (month.lengthOfMonth downTo 1)
            .asSequence()
            .map { day -> month.atDay(day).toGregorian() }
            .firstOrNull(::isDateEnabled)

    private fun nearestSelectableMonthInYear(year: Int, preferredMonth: Int): PersianYearMonth? =
        (1..12)
            .filter { month -> isMonthSelectable(year, month) }
            .minWithOrNull(compareBy<Int> { month -> kotlin.math.abs(month - preferredMonth) }.thenBy { it })
            ?.let { month -> PersianYearMonth(year, month) }

    private fun isEntireBoundaryExplicitlyDisabled(): Boolean {
        val boundaryDayCount = ChronoUnit.DAYS.between(config.minDate, config.maxDate) + 1L
        var explicitlyDisabledDayCount = config.disabledDateRanges.sumOf { range -> range.dayCount }
        if (explicitlyDisabledDayCount >= boundaryDayCount) return true

        disabledDates.forEach { date ->
            if (!isInsideDisabledRange(date)) {
                explicitlyDisabledDayCount += 1L
                if (explicitlyDisabledDayCount >= boundaryDayCount) return true
            }
        }
        return false
    }

    private fun hasDisabledRangeOverlap(start: LocalDate, end: LocalDate): Boolean {
        val ranges = config.disabledDateRanges
        var low = 0
        var high = ranges.lastIndex
        while (low <= high) {
            val middle = (low + high).ushr(1)
            if (ranges[middle].endInclusive < start) {
                low = middle + 1
            } else {
                high = middle - 1
            }
        }
        return low <= ranges.lastIndex && ranges[low].start <= end
    }

    private fun isInsideDisabledRange(date: LocalDate): Boolean {
        var low = 0
        var high = config.disabledDateRanges.lastIndex
        while (low <= high) {
            val middle = (low + high).ushr(1)
            val range = config.disabledDateRanges[middle]
            when {
                date < range.start -> high = middle - 1
                date > range.endInclusive -> low = middle + 1
                else -> return true
            }
        }
        return false
    }

    private fun sanitizeSelection(candidate: PersianDateSelection): PersianDateSelection {
        if (candidate.mode != selectionMode) return PersianDateSelection.empty(selectionMode)

        return when (candidate) {
            is PersianDateSelection.Single -> PersianDateSelection.Single(
                candidate.date?.takeIf(::isDateEnabled),
            )

            is PersianDateSelection.Multiple -> {
                val validDates = candidate.dates
                    .asSequence()
                    .filter(::isDateEnabled)
                    .distinct()
                    .sorted()
                    .let { sequence ->
                        val limit = config.maxMultipleSelection
                        if (limit == null) sequence.toList() else sequence.take(limit).toList()
                    }
                    .toCollection(linkedSetOf())
                PersianDateSelection.Multiple(validDates)
            }

            is PersianDateSelection.Range -> sanitizeRangeSelection(candidate)
        }
    }

    private fun sanitizeRangeSelection(
        candidate: PersianDateSelection.Range,
    ): PersianDateSelection.Range {
        val validStart = candidate.start?.takeIf(::isDateEnabled)
        val validEnd = candidate.end?.takeIf(::isDateEnabled)

        if (validStart == null && validEnd == null) {
            return PersianDateSelection.Range(null, null)
        }
        if (validStart == null || validEnd == null) {
            return PersianDateSelection.Range(validStart ?: validEnd, null)
        }

        val start = minOf(validStart, validEnd)
        val end = maxOf(validStart, validEnd)
        val rangeDays = rangeLengthDays(start, end)
        return if (
            rangeDays < config.minRangeDays ||
            config.maxRangeDays?.let { rangeDays > it } == true ||
            (!config.allowDisabledDatesInsideRange && hasDisabledDateBetween(start, end))
        ) {
            PersianDateSelection.Range(start, null)
        } else {
            PersianDateSelection.Range(start, end)
        }
    }


    private fun rangeLengthDays(start: LocalDate, end: LocalDate): Int =
        (ChronoUnit.DAYS.between(start, end) + 1L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    private fun PersianYearMonth.coerceIn(
        minimum: PersianYearMonth,
        maximum: PersianYearMonth,
    ): PersianYearMonth = when {
        this < minimum -> minimum
        this > maximum -> maximum
        else -> this
    }

    private fun LocalDate.coerceIn(minimum: LocalDate, maximum: LocalDate): LocalDate = when {
        this < minimum -> minimum
        this > maximum -> maximum
        else -> this
    }

    companion object {
        private const val DAYS_PER_WEEK = 7L
        private const val KEY_DISPLAYED_YEAR = "displayedYear"
        private const val KEY_DISPLAYED_MONTH = "displayedMonth"
        private const val KEY_VIEW = "view"
        private const val KEY_SELECTION = "selection"

        internal fun saver(
            selectionMode: PersianDateSelectionMode,
            config: PersianDatePickerConfig,
        ): Saver<PersianDatePickerState, Any> = mapSaver(
            save = { state ->
                mapOf(
                    KEY_DISPLAYED_YEAR to state.displayedMonth.year,
                    KEY_DISPLAYED_MONTH to state.displayedMonth.month,
                    KEY_VIEW to state.view.ordinal,
                    KEY_SELECTION to state.selection.toEpochDays(),
                )
            },
            restore = restoreState@{ restored ->
                val year = restored[KEY_DISPLAYED_YEAR] as? Int ?: return@restoreState null
                val month = restored[KEY_DISPLAYED_MONTH] as? Int ?: return@restoreState null
                val epochDays = when (val raw = restored[KEY_SELECTION]) {
                    is LongArray -> raw
                    is List<*> -> raw.mapNotNull { it as? Long }.toLongArray()
                    else -> longArrayOf()
                }
                try {
                    val selection = selectionFromEpochDays(
                        mode = selectionMode,
                        epochDays = epochDays,
                    )
                    PersianDatePickerState(
                        selectionMode = selectionMode,
                        initialSelection = selection,
                        initialDisplayedMonth = PersianYearMonth(year, month),
                        config = config,
                    ).also { state ->
                        val restoredView = restored[KEY_VIEW] as? Int ?: 0
                        state.view = PersianDatePickerView.entries.getOrElse(restoredView) {
                            PersianDatePickerView.DAYS
                        }
                    }
                } catch (_: IllegalArgumentException) {
                    null
                } catch (_: ArithmeticException) {
                    null
                } catch (_: DateTimeException) {
                    null
                }
            },
        )

        private fun PersianDateSelection.toEpochDays(): LongArray = when (this) {
            is PersianDateSelection.Single -> date?.let { longArrayOf(it.toEpochDay()) } ?: longArrayOf()
            is PersianDateSelection.Range -> listOfNotNull(start, end).map(LocalDate::toEpochDay).toLongArray()
            is PersianDateSelection.Multiple -> dates.sorted().map(LocalDate::toEpochDay).toLongArray()
        }

        private fun selectionFromEpochDays(
            mode: PersianDateSelectionMode,
            epochDays: LongArray,
        ): PersianDateSelection = when (mode) {
            PersianDateSelectionMode.SINGLE -> PersianDateSelection.Single(
                epochDays.firstOrNull()?.let(LocalDate::ofEpochDay),
            )
            PersianDateSelectionMode.RANGE -> PersianDateSelection.Range(
                start = epochDays.getOrNull(0)?.let(LocalDate::ofEpochDay),
                end = epochDays.getOrNull(1)?.let(LocalDate::ofEpochDay),
            )
            PersianDateSelectionMode.MULTIPLE -> PersianDateSelection.Multiple(
                epochDays.map(LocalDate::ofEpochDay).toSortedSet(),
            )
        }
    }
}

@Composable
fun rememberPersianDatePickerState(
    selectionMode: PersianDateSelectionMode = PersianDateSelectionMode.SINGLE,
    initialSelection: PersianDateSelection = PersianDateSelection.empty(selectionMode),
    initialDisplayedDate: LocalDate? = null,
    config: PersianDatePickerConfig = PersianDatePickerConfig(),
    clock: Clock = Clock.systemDefaultZone(),
): PersianDatePickerState {
    val resolvedInitialDate = initialDisplayedDate ?: when (initialSelection) {
        is PersianDateSelection.Single -> initialSelection.date
        is PersianDateSelection.Range -> initialSelection.start
        is PersianDateSelection.Multiple -> initialSelection.dates.minOrNull()
    } ?: LocalDate.now(clock)

    val initialMonth = PersianYearMonth.fromGregorian(
        when {
            resolvedInitialDate < config.minDate -> config.minDate
            resolvedInitialDate > config.maxDate -> config.maxDate
            else -> resolvedInitialDate
        },
    )
    return rememberSaveable(
        selectionMode,
        config,
        saver = PersianDatePickerState.saver(selectionMode, config),
    ) {
        PersianDatePickerState(
            selectionMode = selectionMode,
            initialSelection = initialSelection,
            initialDisplayedMonth = initialMonth,
            config = config,
        )
    }
}
