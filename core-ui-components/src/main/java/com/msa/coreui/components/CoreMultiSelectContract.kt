package com.msa.coreui.components

internal fun <T : Any> validateMultiSelectContract(
    options: List<CoreSelectOption<T>>,
    maxSelection: Int?,
) {
    require(options.map { option -> option.value }.distinct().size == options.size) {
        "Option values must be unique."
    }
    require(maxSelection == null || maxSelection > 0) {
        "maxSelection must be null or greater than zero."
    }
}

internal fun <T : Any> toggleMultiSelection(
    current: Set<T>,
    value: T,
): Set<T> = LinkedHashSet<T>(current).apply {
    if (!add(value)) remove(value)
}.toSet()

internal fun <T : Any> sanitizeMultiSelection(
    selectedValues: Set<T>,
    availableValues: Set<T>,
): Set<T> = selectedValues.filterTo(linkedSetOf(), availableValues::contains).toSet()
