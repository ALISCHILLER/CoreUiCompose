package com.msa.coreui.catalog

/** Stable identifiers for catalog navigation and screenshot automation. */
internal object CatalogTestTags {
    const val HOME_SCREEN = "catalog.screen.home"
    const val COMPONENTS_SCREEN = "catalog.screen.components"
    const val CALENDAR_SCREEN = "catalog.screen.calendar"
    const val SETTINGS_SCREEN = "catalog.screen.settings"
    const val NAVIGATION_SCREEN = "catalog.screen.navigation"
    const val LAYOUT_SCREEN = "catalog.screen.layout"
    const val HEADER_SETTINGS = "catalog.header.settings"

    fun category(id: String): String = "catalog.category.$id"
}
