package com.msa.coreui.consumer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msa.coreui.calendar.PersianCalendar
import com.msa.coreui.calendar.PersianDatePickerConfig
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.PersianDateSelectionFormatter
import com.msa.coreui.calendar.rememberPersianDatePickerState
import com.msa.coreui.calendar.model.GregorianDateRange
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.components.CoreAsyncContent
import com.msa.coreui.components.CoreAvatar
import com.msa.coreui.components.CoreAvatarStatus
import com.msa.coreui.components.CoreTabItem
import com.msa.coreui.components.CoreTabs
import com.msa.coreui.components.CoreAsyncState
import com.msa.coreui.components.CoreBadge
import com.msa.coreui.components.CoreBadgeStyle
import com.msa.coreui.components.CoreSearchField
import com.msa.coreui.components.CorePasswordField
import com.msa.coreui.components.CorePagination
import com.msa.coreui.components.CoreSlider
import com.msa.coreui.components.CoreSegmentItem
import com.msa.coreui.components.CoreSegmentedControl
import com.msa.coreui.components.CoreSelectField
import com.msa.coreui.components.CoreMultiSelectField
import com.msa.coreui.components.CoreFormSection
import com.msa.coreui.components.CoreSelectOption
import com.msa.coreui.components.CoreScreen
import com.msa.coreui.components.CoreSectionHeader
import com.msa.coreui.foundation.CoreUiTheme
import java.time.DayOfWeek
import java.time.LocalDate

/** Compile-only consumer proving that one `core-ui` dependency exposes every public module. */
@Composable
fun ConsumerSmokeScreen(modifier: Modifier = Modifier) {
    val nowruz = LocalDate.of(2024, 3, 20)
    val config = PersianDatePickerConfig(
        minDate = nowruz.minusYears(1),
        maxDate = nowruz.plusYears(1),
        disabledDateRanges = listOf(GregorianDateRange(nowruz.plusDays(1), nowruz.plusDays(3))),
        disabledWeekdays = setOf(DayOfWeek.FRIDAY),
    )

    val pickerState = rememberPersianDatePickerState(config = config)

    CoreUiTheme {
        CoreScreen(title = "Consumer smoke", modifier = modifier) { contentPadding ->
            Column(Modifier.padding(contentPadding)) {
                CoreSectionHeader(
                    title = "Core UI",
                    supportingText = "Published component contract",
                )
                CoreBadge(
                    text = PersianCalendar.fromGregorian(nowruz).toString(),
                    style = CoreBadgeStyle.SUCCESS,
                )
                Text(
                    PersianDateSelectionFormatter.format(
                        selection = PersianDateSelection.Single(nowruz),
                        strings = PersianDatePickerStrings.English,
                        usePersianDigits = false,
                    ),
                )
                CoreAvatar(
                    label = "MSA Core UI",
                    contentDescription = "MSA Core UI",
                    status = CoreAvatarStatus.ONLINE,
                    statusContentDescription = "Online",
                )
                CoreTabs(
                    items = listOf(CoreTabItem("Overview"), CoreTabItem("Calendar")),
                    selectedIndex = 0,
                    onSelected = {},
                )
                CoreSearchField(
                    query = "",
                    onQueryChange = {},
                    onSearch = {},
                )
                CorePasswordField(
                    value = "consumer",
                    onValueChange = {},
                    label = "Password",
                    passwordVisible = false,
                    onPasswordVisibilityChange = {},
                )
                CoreSlider(
                    value = 0.5f,
                    onValueChange = {},
                    label = "Progress threshold",
                )
                CorePagination(
                    currentPage = 1,
                    pageCount = 3,
                    onPageSelected = {},
                    showBoundaryActions = false,
                )
                CoreSegmentedControl(
                    items = listOf(CoreSegmentItem("UI"), CoreSegmentItem("Calendar")),
                    selectedIndex = 0,
                    onSelected = {},
                )
                CoreSelectField(
                    options = listOf(CoreSelectOption("stable", "Stable"), CoreSelectOption("preview", "Preview")),
                    selectedValue = "stable",
                    onValueSelected = {},
                    label = "Channel",
                )
                CoreFormSection(
                    title = "Capabilities",
                    description = "Multi-select and structured form layout",
                ) {
                    CoreMultiSelectField(
                        options = listOf(
                            CoreSelectOption("ui", "UI"),
                            CoreSelectOption("calendar", "Calendar"),
                            CoreSelectOption("a11y", "Accessibility"),
                        ),
                        selectedValues = setOf("ui", "calendar"),
                        onSelectionChange = {},
                        label = "Modules",
                    )
                }
                CoreAsyncContent(
                    state = CoreAsyncState.Success(config.maxDate),
                    content = {
                        Text("$it: ${pickerState.dateAvailability(nowruz)}")
                    },
                    empty = { Text("Empty") },
                )
            }
        }
    }
}
