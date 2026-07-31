package com.msa.coreui.catalog

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

internal enum class CatalogThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

internal enum class CatalogLanguageMode {
    SYSTEM,
    PERSIAN,
    ENGLISH,
}

internal enum class CatalogAccent(
    val primary: Color,
    val primaryContainer: Color,
    val darkPrimary: Color,
    val darkPrimaryContainer: Color,
) {
    PURPLE(Color(0xFF5B3DF5), Color(0xFFE7DEFF), Color(0xFFCBBEFF), Color(0xFF4630B6)),
    BLUE(Color(0xFF1769E0), Color(0xFFD8E5FF), Color(0xFFAEC7FF), Color(0xFF004397)),
    TEAL(Color(0xFF008A95), Color(0xFFA7F0F3), Color(0xFF68D9E0), Color(0xFF006066)),
    GREEN(Color(0xFF2F8F46), Color(0xFFB8F3C2), Color(0xFF92DC9E), Color(0xFF17652C)),
    ORANGE(Color(0xFFE97913), Color(0xFFFFDCC2), Color(0xFFFFB77A), Color(0xFFA54800)),
    RED(Color(0xFFD13A4A), Color(0xFFFFDADB), Color(0xFFFFB3B8), Color(0xFF8E2030)),
}

@Immutable
internal data class CatalogSettings(
    val themeMode: CatalogThemeMode = CatalogThemeMode.SYSTEM,
    val languageMode: CatalogLanguageMode = CatalogLanguageMode.PERSIAN,
    val animationsEnabled: Boolean = true,
    val persianDigits: Boolean = true,
    val accent: CatalogAccent = CatalogAccent.PURPLE,
)

/**
 * Small, defensive codec kept separate from Android storage so corrupt or future preference values
 * can be tested without an emulator and always fall back to a valid catalog configuration.
 */
internal object CatalogSettingsCodec {
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_LANGUAGE_MODE = "language_mode"
    private const val KEY_ANIMATIONS_ENABLED = "animations_enabled"
    private const val KEY_PERSIAN_DIGITS = "persian_digits"
    private const val KEY_ACCENT = "accent"

    fun decode(values: Map<String, *>): CatalogSettings = CatalogSettings(
        themeMode = enumValue(values[KEY_THEME_MODE], CatalogThemeMode.SYSTEM),
        languageMode = enumValue(values[KEY_LANGUAGE_MODE], CatalogLanguageMode.PERSIAN),
        animationsEnabled = values[KEY_ANIMATIONS_ENABLED] as? Boolean ?: true,
        persianDigits = values[KEY_PERSIAN_DIGITS] as? Boolean ?: true,
        accent = enumValue(values[KEY_ACCENT], CatalogAccent.PURPLE),
    )

    fun encode(settings: CatalogSettings): Map<String, Any> = linkedMapOf(
        KEY_THEME_MODE to settings.themeMode.name,
        KEY_LANGUAGE_MODE to settings.languageMode.name,
        KEY_ANIMATIONS_ENABLED to settings.animationsEnabled,
        KEY_PERSIAN_DIGITS to settings.persianDigits,
        KEY_ACCENT to settings.accent.name,
    )

    private inline fun <reified T : Enum<T>> enumValue(value: Any?, fallback: T): T {
        val name = value as? String ?: return fallback
        return enumValues<T>().firstOrNull { candidate -> candidate.name == name } ?: fallback
    }
}

internal interface CatalogSettingsStorage {
    fun load(): CatalogSettings
    fun save(settings: CatalogSettings)
    fun clear()
}

internal class CatalogPreferences(context: Context) : CatalogSettingsStorage {
    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(
        FILE_NAME,
        Context.MODE_PRIVATE,
    )

    override fun load(): CatalogSettings = CatalogSettingsCodec.decode(preferences.all)

    override fun save(settings: CatalogSettings) {
        val values = CatalogSettingsCodec.encode(settings)
        preferences.edit()
            .putString("theme_mode", values.getValue("theme_mode") as String)
            .putString("language_mode", values.getValue("language_mode") as String)
            .putBoolean("animations_enabled", values.getValue("animations_enabled") as Boolean)
            .putBoolean("persian_digits", values.getValue("persian_digits") as Boolean)
            .putString("accent", values.getValue("accent") as String)
            .apply()
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "msa_core_ui_catalog_preferences"
    }
}
