package com.msa.coreui.calendar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.msa.coreui.foundation.CoreUiTheme

@Immutable
data class PersianDatePickerColors(
    val container: Color,
    val headerContainer: Color,
    val headerContent: Color,
    val selectedContainer: Color,
    val selectedContent: Color,
    val rangeContainer: Color,
    val todayBorder: Color,
    val dayContent: Color,
    val disabledContent: Color,
    val adjacentMonthContent: Color,
)

object PersianDatePickerDefaults {
    @Composable
    fun colors(): PersianDatePickerColors = PersianDatePickerColors(
        container = MaterialTheme.colorScheme.surface,
        headerContainer = MaterialTheme.colorScheme.primaryContainer,
        headerContent = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedContainer = MaterialTheme.colorScheme.primary,
        selectedContent = MaterialTheme.colorScheme.onPrimary,
        rangeContainer = MaterialTheme.colorScheme.secondaryContainer,
        todayBorder = MaterialTheme.colorScheme.primary,
        dayContent = MaterialTheme.colorScheme.onSurface,
        disabledContent = MaterialTheme.colorScheme.onSurface.copy(alpha = CoreUiTheme.alpha.disabledContent),
        adjacentMonthContent = MaterialTheme.colorScheme.onSurface.copy(alpha = CoreUiTheme.alpha.mutedContent),
    )
}
