package com.msa.coreui.mavenconsumer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msa.coreui.CoreUi
import com.msa.coreui.calendar.PersianCalendar
import com.msa.coreui.components.CoreAsyncContent
import com.msa.coreui.components.CoreAvatar
import com.msa.coreui.components.CoreAvatarStatus
import com.msa.coreui.components.CoreSkeletonText
import com.msa.coreui.components.CoreAsyncState
import com.msa.coreui.components.CoreBadge
import com.msa.coreui.components.CoreBadgeStyle
import com.msa.coreui.components.CoreBanner
import com.msa.coreui.components.CoreBannerStyle
import com.msa.coreui.components.CoreScreen
import com.msa.coreui.components.CoreSegmentItem
import com.msa.coreui.components.CoreSegmentedControl
import com.msa.coreui.components.CorePasswordField
import com.msa.coreui.components.CorePagination
import com.msa.coreui.components.CoreSlider
import com.msa.coreui.components.CoreSelectField
import com.msa.coreui.components.CoreMultiSelectField
import com.msa.coreui.components.CoreFormSection
import com.msa.coreui.components.CoreSelectOption
import com.msa.coreui.components.CoreSectionHeader
import com.msa.coreui.foundation.CoreUiTheme
import java.time.LocalDate

/** Compiles only after aggregate artifacts and transitive Maven metadata are published. */
@Composable
fun MavenConsumerScreen() {
    val nowruz = PersianCalendar.fromGregorian(LocalDate.of(2024, 3, 20))
    CoreUiTheme {
        CoreScreen(title = CoreUi.name) { contentPadding ->
            Column(Modifier.padding(contentPadding)) {
                CoreSectionHeader(
                    title = "Core UI",
                    supportingText = "Published component contract",
                )
                CoreBanner(
                    title = CoreUi.name,
                    message = "Resolved ${CoreUi.version}; Nowruz is $nowruz",
                    style = CoreBannerStyle.SUCCESS,
                )
                CoreAvatar(
                    label = CoreUi.name,
                    contentDescription = CoreUi.name,
                    status = CoreAvatarStatus.ONLINE,
                    statusContentDescription = "Online",
                )
                CoreBadge(
                    text = "Maven metadata resolved",
                    style = CoreBadgeStyle.INFO,
                )
                CorePasswordField(
                    value = "metadata",
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
                    items = listOf(CoreSegmentItem("Aggregate"), CoreSegmentItem("Modules")),
                    selectedIndex = 0,
                    onSelected = {},
                )
                CoreSelectField(
                    options = listOf(CoreSelectOption("maven", "Maven"), CoreSelectOption("project", "Project")),
                    selectedValue = "maven",
                    onValueSelected = {},
                    label = "Resolution source",
                )
                CoreFormSection(title = "Published multi-select") {
                    CoreMultiSelectField(
                        options = listOf(
                            CoreSelectOption("foundation", "Foundation"),
                            CoreSelectOption("components", "Components"),
                            CoreSelectOption("calendar", "Calendar"),
                        ),
                        selectedValues = setOf("foundation", "components"),
                        onSelectionChange = {},
                        label = "Artifacts",
                    )
                }
                CoreSkeletonText(lines = 2)
                CoreAsyncContent(
                    state = CoreAsyncState.Success(nowruz),
                    content = { Text(it.toString()) },
                    empty = { Text("Empty") },
                )
            }
        }
    }
}
