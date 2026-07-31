package com.msa.coreui.components

/** Type-safe option consumed by [CoreSelectField] and [CoreMultiSelectField]. */
data class CoreSelectOption<T : Any>(
    val value: T,
    val label: String,
    val contentDescription: String = label,
    val enabled: Boolean = true,
) {
    init {
        require(label.isNotBlank()) { "label must not be blank." }
        require(contentDescription.isNotBlank()) { "contentDescription must not be blank." }
    }
}
