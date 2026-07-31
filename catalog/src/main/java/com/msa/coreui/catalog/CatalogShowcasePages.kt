package com.msa.coreui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msa.coreui.components.CoreAdaptiveGrid
import com.msa.coreui.components.CoreBanner
import com.msa.coreui.components.CoreBannerStyle
import com.msa.coreui.components.CoreButton
import com.msa.coreui.components.CoreButtonContent
import com.msa.coreui.components.CoreButtonStyle
import com.msa.coreui.components.CoreCard
import com.msa.coreui.components.CoreCardStyle
import com.msa.coreui.components.CoreIcon
import com.msa.coreui.components.CoreIcons
import com.msa.coreui.components.CoreListItem
import com.msa.coreui.components.CoreNavigationBar
import com.msa.coreui.components.CoreNavigationItem
import com.msa.coreui.components.CoreNavigationRail
import com.msa.coreui.components.CoreResponsiveFormRow
import com.msa.coreui.components.CoreResponsivePane
import com.msa.coreui.components.CoreSectionHeader
import com.msa.coreui.components.CoreStyledCard
import com.msa.coreui.foundation.CoreContentContainer
import com.msa.coreui.foundation.CoreUiWindowSizeClass

@Composable
internal fun NavigationShowcasePage(
    modifier: Modifier,
    rtlMode: Boolean,
) {
    val navigationItems = listOf(
        CoreNavigationItem(catalogText(rtlMode, "خانه", "Home")),
        CoreNavigationItem(
            label = catalogText(rtlMode, "پیام‌ها", "Messages"),
            badgeText = if (rtlMode) "۳" else "3",
            badgeContentDescription = catalogText(rtlMode, "۳ پیام خوانده‌نشده", "3 unread messages"),
        ),
        CoreNavigationItem(catalogText(rtlMode, "پروفایل", "Profile")),
    )
    var bottomSelectedIndex by rememberSaveable { mutableStateOf(0) }
    var railSelectedIndex by rememberSaveable { mutableStateOf(0) }

    CoreContentContainer(
        modifier = modifier
            .fillMaxSize()
            .testTag(CatalogTestTags.NAVIGATION_SCREEN),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                CoreSectionHeader(
                    title = catalogText(rtlMode, "الگوهای ناوبری", "Navigation patterns"),
                    supportingText = catalogText(
                        rtlMode,
                        "نمونه‌های واقعی Bottom bar، Navigation rail، badge و state کنترل‌شده",
                        "Real bottom bar, navigation rail, badge, and controlled-state examples",
                    ),
                )
            }
            item {
                CoreBanner(
                    title = catalogText(rtlMode, "ناوبری اکنون یک مقصد واقعی دارد", "Navigation is now a real destination"),
                    message = catalogText(
                        rtlMode,
                        "انتخاب هر آیتم در نمونه‌ها state را تغییر می‌دهد و پس از بازسازی Activity حفظ می‌شود.",
                        "Selecting an item updates controlled state and survives activity recreation.",
                    ),
                    style = CoreBannerStyle.SUCCESS,
                    icon = { CoreIcon(CoreIcons.Check, null, decorative = true) },
                )
            }
            item {
                CoreStyledCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = CoreCardStyle.ELEVATED,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = catalogText(rtlMode, "نوار ناوبری پایین", "Bottom navigation"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = catalogText(
                                    rtlMode,
                                    "برای ۲ تا ۵ مقصد اصلی در عرض‌های فشرده",
                                    "For two to five primary destinations on compact widths",
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        CoreNavigationBar(
                            items = navigationItems,
                            modifier = Modifier.fillMaxWidth(),
                            selectedIndex = bottomSelectedIndex,
                            onSelected = { bottomSelectedIndex = it },
                            icon = { item, _ -> NavigationDemoIcon(navigationItems, item) },
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        )
                        Text(
                            text = catalogText(rtlMode, "مقصد فعال: ", "Active destination: ") +
                                navigationItems[bottomSelectedIndex].label,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            item {
                CoreSectionHeader(
                    title = catalogText(rtlMode, "Navigation rail", "Navigation rail"),
                    supportingText = catalogText(
                        rtlMode,
                        "برای تبلت، پنجره‌های عریض و مقصدهای پایدار",
                        "For tablets, wide windows, and stable destinations",
                    ),
                )
            }
            item {
                CoreCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                    ) {
                        CoreNavigationRail(
                            items = navigationItems,
                            selectedIndex = railSelectedIndex,
                            onSelected = { railSelectedIndex = it },
                            icon = { item, _ -> NavigationDemoIcon(navigationItems, item) },
                            modifier = Modifier.fillMaxHeight(),
                            header = {
                                Surface(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .size(40.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("M", fontWeight = FontWeight.Black)
                                    }
                                }
                            },
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CoreIcon(
                                imageVector = navigationDemoIcons[railSelectedIndex],
                                contentDescription = null,
                                decorative = true,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = navigationItems[railSelectedIndex].label,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = catalogText(
                                    rtlMode,
                                    "محتوای مقصد انتخاب‌شده اینجا نمایش داده می‌شود.",
                                    "The selected destination content is rendered here.",
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            item {
                CoreCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        NavigationGuideline(
                            title = catalogText(rtlMode, "State متعلق به caller", "Caller-owned state"),
                            message = catalogText(
                                rtlMode,
                                "selectedIndex و رویداد onSelected صریح هستند؛ کامپوننت route پنهان نمی‌سازد.",
                                "selectedIndex and onSelected are explicit; the component creates no hidden route state.",
                            ),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        NavigationGuideline(
                            title = catalogText(rtlMode, "مقصد جزئی جدا از نوار اصلی", "Detail destinations stay separate"),
                            message = catalogText(
                                rtlMode,
                                "صفحه‌های Navigation و Layout بدون افزایش مقصدهای Bottom bar باز می‌شوند.",
                                "Navigation and Layout open without increasing the bottom-bar destination count.",
                            ),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        NavigationGuideline(
                            title = catalogText(rtlMode, "RTL و دسترس‌پذیری", "RTL and accessibility"),
                            message = catalogText(
                                rtlMode,
                                "label، badge و جهت آیکن بازگشت برای فارسی و انگلیسی مستقل بررسی می‌شوند.",
                                "Labels, badges, and back-icon direction are handled independently for Persian and English.",
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun LayoutShowcasePage(
    modifier: Modifier,
    rtlMode: Boolean,
) {
    var selectedPaneIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val layoutSamples = listOf(
        LayoutSample(
            title = catalogText(rtlMode, "Scaffold", "Scaffold"),
            description = catalogText(rtlMode, "Top bar، محتوا، feedback و action", "Top bar, content, feedback, and action"),
            icon = CoreIcons.Menu,
        ),
        LayoutSample(
            title = catalogText(rtlMode, "Adaptive grid", "Adaptive grid"),
            description = catalogText(rtlMode, "تعداد ستون خودکار براساس عرض", "Automatic columns based on width"),
            icon = CoreIcons.Home,
        ),
        LayoutSample(
            title = catalogText(rtlMode, "Master-detail", "Master-detail"),
            description = catalogText(rtlMode, "یک pane در موبایل و دو pane در عرض بزرگ", "One pane on phones and two panes on wider screens"),
            icon = CoreIcons.ChevronRight,
        ),
        LayoutSample(
            title = catalogText(rtlMode, "Responsive form", "Responsive form"),
            description = catalogText(rtlMode, "ستونی در compact و دوتایی در medium/expanded", "Stacked on compact and paired on medium/expanded"),
            icon = CoreIcons.Check,
        ),
    )

    CoreContentContainer(
        modifier = modifier
            .fillMaxSize()
            .testTag(CatalogTestTags.LAYOUT_SCREEN),
    ) { windowClass ->
        CoreAdaptiveGrid(
            modifier = Modifier.fillMaxSize(),
            minimumCellWidth = 220.dp,
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            horizontalSpacing = 16.dp,
            verticalSpacing = 16.dp,
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CoreSectionHeader(
                    title = catalogText(rtlMode, "چیدمان واکنش‌گرا", "Responsive layout"),
                    supportingText = catalogText(
                        rtlMode,
                        "صفحه را در موبایل، تبلت یا حالت split-screen تغییر اندازه دهید.",
                        "Resize the screen across phone, tablet, or split-screen widths.",
                    ),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CoreBanner(
                    title = catalogText(rtlMode, "کلاس عرض فعلی", "Current width class"),
                    message = windowClass.localizedName(rtlMode),
                    style = CoreBannerStyle.INFO,
                    icon = { CoreIcon(CoreIcons.Info, null, decorative = true) },
                )
            }
            gridItems(layoutSamples, key = LayoutSample::title) { sample ->
                CoreStyledCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = CoreCardStyle.ELEVATED,
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CoreIcon(
                                    imageVector = sample.icon,
                                    contentDescription = null,
                                    decorative = true,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                        Text(
                            text = sample.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = sample.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CoreSectionHeader(
                    title = catalogText(rtlMode, "ردیف فرم واکنش‌گرا", "Responsive form row"),
                    supportingText = catalogText(
                        rtlMode,
                        "این دو فیلد مفهومی در عرض فشرده زیر هم و در عرض بزرگ کنار هم قرار می‌گیرند.",
                        "These conceptual fields stack on compact widths and sit side by side on wider widths.",
                    ),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CoreResponsiveFormRow(
                    first = {
                        LayoutFieldPreview(
                            label = catalogText(rtlMode, "نام پروژه", "Project name"),
                            value = "MSA Core UI",
                        )
                    },
                    second = {
                        LayoutFieldPreview(
                            label = catalogText(rtlMode, "وضعیت انتشار", "Release status"),
                            value = catalogText(rtlMode, "نامزد انتشار", "Release candidate"),
                        )
                    },
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CoreSectionHeader(
                    title = catalogText(rtlMode, "Master-detail واکنش‌گرا", "Responsive master-detail"),
                    supportingText = catalogText(
                        rtlMode,
                        "در موبایل ابتدا فهرست و سپس جزئیات؛ در عرض بزرگ هر دو هم‌زمان",
                        "List then detail on phones; both panes together on wider screens",
                    ),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CoreCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                ) {
                    CoreResponsivePane(
                        showDetailOnCompact = selectedPaneIndex != null,
                        modifier = Modifier.fillMaxSize(),
                        primary = {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                            ) {
                                listItems(layoutSamples, key = LayoutSample::title) { sample ->
                                    val index = layoutSamples.indexOf(sample)
                                    CoreListItem(
                                        title = sample.title,
                                        supportingText = sample.description,
                                        selected = selectedPaneIndex == index,
                                        onClick = { selectedPaneIndex = index },
                                        leading = {
                                            CoreIcon(sample.icon, null, decorative = true)
                                        },
                                        trailing = {
                                            CoreIcon(
                                                imageVector = if (rtlMode) CoreIcons.ChevronLeft else CoreIcons.ChevronRight,
                                                contentDescription = null,
                                                decorative = true,
                                            )
                                        },
                                    )
                                }
                            }
                        },
                        detail = { detailWindowClass ->
                            LayoutDetailPane(
                                sample = selectedPaneIndex?.let(layoutSamples::get),
                                windowClass = detailWindowClass,
                                rtlMode = rtlMode,
                                onBack = { selectedPaneIndex = null },
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationGuideline(
    title: String,
    message: String,
) {
    CoreListItem(
        title = title,
        supportingText = message,
        leading = { CoreIcon(CoreIcons.Check, null, decorative = true) },
    )
}

@Composable
private fun NavigationDemoIcon(
    items: List<CoreNavigationItem>,
    item: CoreNavigationItem,
) {
    val index = items.indexOf(item).coerceIn(navigationDemoIcons.indices)
    CoreIcon(
        imageVector = navigationDemoIcons[index],
        contentDescription = null,
        decorative = true,
    )
}

@Composable
private fun LayoutFieldPreview(
    label: String,
    value: String,
) {
    CoreStyledCard(
        modifier = Modifier.fillMaxWidth(),
        style = CoreCardStyle.FILLED,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LayoutDetailPane(
    sample: LayoutSample?,
    windowClass: CoreUiWindowSizeClass,
    rtlMode: Boolean,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (sample == null) {
            CoreIcon(
                imageVector = CoreIcons.Menu,
                contentDescription = null,
                decorative = true,
                modifier = Modifier.size(42.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = catalogText(rtlMode, "یک الگو را انتخاب کنید", "Select a pattern"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        } else {
            CoreIcon(
                imageVector = sample.icon,
                contentDescription = null,
                decorative = true,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = sample.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = sample.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = windowClass.localizedName(rtlMode),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (windowClass == CoreUiWindowSizeClass.COMPACT && sample != null) {
            Spacer(Modifier.height(20.dp))
            CoreButton(
                content = CoreButtonContent(catalogText(rtlMode, "بازگشت به فهرست", "Back to list")),
                onClick = onBack,
                style = CoreButtonStyle.SECONDARY,
                leadingIcon = {
                    CoreIcon(
                        imageVector = if (rtlMode) CoreIcons.ChevronRight else CoreIcons.ArrowBack,
                        contentDescription = null,
                        decorative = true,
                    )
                },
            )
        }
    }
}

private data class LayoutSample(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private val navigationDemoIcons = listOf(
    CoreIcons.Home,
    CoreIcons.Info,
    CoreIcons.Settings,
)

private fun CoreUiWindowSizeClass.localizedName(rtlMode: Boolean): String = when (this) {
    CoreUiWindowSizeClass.COMPACT -> catalogText(rtlMode, "Compact - موبایل و پنجره باریک", "Compact - phone or narrow window")
    CoreUiWindowSizeClass.MEDIUM -> catalogText(rtlMode, "Medium - تبلت کوچک یا split-screen", "Medium - small tablet or split screen")
    CoreUiWindowSizeClass.EXPANDED -> catalogText(rtlMode, "Expanded - تبلت و پنجره عریض", "Expanded - tablet or wide window")
}

private fun catalogText(rtlMode: Boolean, fa: String, en: String): String = if (rtlMode) fa else en
