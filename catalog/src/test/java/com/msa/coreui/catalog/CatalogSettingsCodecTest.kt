package com.msa.coreui.catalog

import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogSettingsCodecTest {
    @Test
    fun emptyAndCorruptValuesFallBackToSafeDefaults() {
        assertEquals(CatalogSettings(), CatalogSettingsCodec.decode(emptyMap<String, Any>()))

        val decoded = CatalogSettingsCodec.decode(
            mapOf(
                "theme_mode" to "UNKNOWN",
                "language_mode" to 42,
                "animations_enabled" to "true",
                "persian_digits" to null,
                "accent" to "MISSING",
            ),
        )

        assertEquals(CatalogSettings(), decoded)
    }

    @Test
    fun settingsRoundTripThroughCodec() {
        val expected = CatalogSettings(
            themeMode = CatalogThemeMode.DARK,
            languageMode = CatalogLanguageMode.ENGLISH,
            animationsEnabled = false,
            persianDigits = false,
            accent = CatalogAccent.TEAL,
        )

        assertEquals(expected, CatalogSettingsCodec.decode(CatalogSettingsCodec.encode(expected)))
    }
}
