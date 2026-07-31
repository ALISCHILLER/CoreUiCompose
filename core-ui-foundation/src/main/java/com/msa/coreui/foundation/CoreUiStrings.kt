package com.msa.coreui.foundation

import androidx.compose.runtime.Immutable
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Shared accessibility and fallback labels used by Core UI primitives.
 *
 * Product-facing copy should still come from the consuming application's resources. Override this
 * value at [CoreUiTheme] whenever the application locale changes.
 */
@Immutable
data class CoreUiStrings(
    val loading: String = "Loading",
    val retry: String = "Retry",
    val clear: String = "Clear",
    val search: String = "Search",
    val close: String = "Close",
    val back: String = "Back",
    val menu: String = "Menu",
    val moreOptions: String = "More options",
    val showPassword: String = "Show password",
    val hidePassword: String = "Hide password",
    val selected: String = "Selected",
    val expanded: String = "Expanded",
    val collapsed: String = "Collapsed",
    val required: String = "Required",
    val optional: String = "Optional",
    val previous: String = "Previous",
    val next: String = "Next",
    val dismiss: String = "Dismiss",
    val open: String = "Open",
    val expand: String = "Expand",
    val collapse: String = "Collapse",
    val notSelected: String = "Not selected",
    val noOptions: String = "No options available",
    val completed: String = "Completed",
    val current: String = "Current",
    val upcoming: String = "Upcoming",
    val error: String = "Error",
    val online: String = "Online",
    val away: String = "Away",
    val busy: String = "Busy",
    val offline: String = "Offline",
    val validationSummary: String = "Please review the following",
    val selectionUnavailable: String = "The selected value is no longer available",
    val firstPage: String = "First page",
    val lastPage: String = "Last page",
    val page: String = "Page",
    val formatNumber: (Int) -> String = Int::toString,
    val formatDecimal: (Float) -> String = ::formatDecimalNumber,
    val formatPercentage: (Float) -> String = { value -> "${formatDecimalNumber(value * 100f)}%" },
    val charactersRemaining: (Int) -> String = { count -> "$count characters remaining" },
    val charactersOverLimit: (Int) -> String = { count -> "$count characters over the limit" },
    val stepOf: (Int, Int) -> String = { index, total -> "Step $index of $total" },
    val pageOf: (Int, Int) -> String = { index, total -> "Page $index of $total" },
    val itemsSelected: (Int) -> String = { count -> if (count == 1) "1 item selected" else "$count items selected" },
    val selectionLimitReached: (Int) -> String = { count -> "Selection limit of $count reached" },
) {
    init {
        val labels = listOf(
            loading,
            retry,
            clear,
            search,
            close,
            back,
            menu,
            moreOptions,
            showPassword,
            hidePassword,
            selected,
            expanded,
            collapsed,
            required,
            optional,
            previous,
            next,
            dismiss,
            open,
            expand,
            collapse,
            notSelected,
            noOptions,
            completed,
            current,
            upcoming,
            error,
            online,
            away,
            busy,
            offline,
            validationSummary,
            selectionUnavailable,
            firstPage,
            lastPage,
            page,
        )
        require(labels.all(String::isNotBlank)) { "Core UI strings must not be blank." }
        require(formatNumber(0).isNotBlank()) { "formatNumber must return non-blank text." }
        require(formatDecimal(0f).isNotBlank()) { "formatDecimal must return non-blank text." }
        require(formatPercentage(0f).isNotBlank()) { "formatPercentage must return non-blank text." }
        require(charactersRemaining(0).isNotBlank()) { "charactersRemaining must return non-blank text." }
        require(charactersOverLimit(1).isNotBlank()) { "charactersOverLimit must return non-blank text." }
        require(stepOf(1, 1).isNotBlank()) { "stepOf must return non-blank text." }
        require(pageOf(1, 1).isNotBlank()) { "pageOf must return non-blank text." }
        require(itemsSelected(1).isNotBlank()) { "itemsSelected must return non-blank text." }
        require(selectionLimitReached(1).isNotBlank()) { "selectionLimitReached must return non-blank text." }
    }

    companion object {
        val English: CoreUiStrings = CoreUiStrings()
        val Persian: CoreUiStrings = CoreUiStrings(
            loading = "در حال بارگذاری",
            retry = "تلاش دوباره",
            clear = "پاک کردن",
            search = "جستجو",
            close = "بستن",
            back = "بازگشت",
            menu = "منو",
            moreOptions = "گزینه‌های بیشتر",
            showPassword = "نمایش رمز عبور",
            hidePassword = "پنهان کردن رمز عبور",
            selected = "انتخاب شده",
            expanded = "باز شده",
            collapsed = "بسته شده",
            required = "الزامی",
            optional = "اختیاری",
            previous = "قبلی",
            next = "بعدی",
            dismiss = "رد کردن",
            open = "باز کردن",
            expand = "باز کردن بخش",
            collapse = "بستن بخش",
            notSelected = "انتخاب نشده",
            noOptions = "گزینه‌ای موجود نیست",
            completed = "تکمیل شده",
            current = "در حال انجام",
            upcoming = "پیش رو",
            error = "خطا",
            online = "آنلاین",
            away = "دور از دسترس",
            busy = "مشغول",
            offline = "آفلاین",
            validationSummary = "موارد زیر را بررسی کنید",
            selectionUnavailable = "مقدار انتخاب‌شده دیگر در دسترس نیست",
            firstPage = "صفحه اول",
            lastPage = "صفحه آخر",
            page = "صفحه",
            formatNumber = Int::toPersianDigits,
            formatDecimal = { value -> formatDecimalNumber(value).toPersianDigits() },
            formatPercentage = { value -> "${formatDecimalNumber(value * 100f).toPersianDigits()}٪" },
            charactersRemaining = { count -> "${count.toPersianDigits()} نویسه باقی مانده" },
            charactersOverLimit = { count -> "${count.toPersianDigits()} نویسه بیشتر از حد مجاز" },
            stepOf = { index, total -> "مرحله ${index.toPersianDigits()} از ${total.toPersianDigits()}" },
            pageOf = { index, total -> "صفحه ${index.toPersianDigits()} از ${total.toPersianDigits()}" },
            itemsSelected = { count -> "${count.toPersianDigits()} مورد انتخاب شده" },
            selectionLimitReached = { count -> "حداکثر ${count.toPersianDigits()} مورد قابل انتخاب است" },
        )
    }
}

private fun Int.toPersianDigits(): String = toString().toPersianDigits()

private fun formatDecimalNumber(value: Float): String {
    require(value.isFinite()) { "Decimal values must be finite." }
    return BigDecimal(value.toString())
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}

private fun String.toPersianDigits(): String = map { character ->
    if (character in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[character - '0'] else character
}.joinToString(separator = "")
