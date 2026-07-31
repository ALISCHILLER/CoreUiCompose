package com.msa.coreui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.coreui.CoreUi
import com.msa.coreui.calendar.PersianDateDigitStyle
import com.msa.coreui.calendar.PersianDatePicker
import com.msa.coreui.calendar.PersianDatePickerConfig
import com.msa.coreui.calendar.PersianDatePickerStrings
import com.msa.coreui.calendar.PersianDateSelectionFormatter
import com.msa.coreui.calendar.model.PersianDateSelection
import com.msa.coreui.calendar.model.PersianDateSelectionMode
import com.msa.coreui.calendar.rememberPersianDatePickerState
import com.msa.coreui.components.CoreAlertDialog
import com.msa.coreui.components.CoreAvatar
import com.msa.coreui.components.CoreAvatarStatus
import com.msa.coreui.components.CoreBanner
import com.msa.coreui.components.CoreBannerStyle
import com.msa.coreui.components.CoreButton
import com.msa.coreui.components.CoreButtonContent
import com.msa.coreui.components.CoreButtonStyle
import com.msa.coreui.components.CoreCard
import com.msa.coreui.components.CoreCardStyle
import com.msa.coreui.components.CoreChip
import com.msa.coreui.components.CoreChipStyle
import com.msa.coreui.components.CoreFieldStatus
import com.msa.coreui.components.CoreIcon
import com.msa.coreui.components.CoreIconButton
import com.msa.coreui.components.CoreIcons
import com.msa.coreui.components.CoreMultiSelectField
import com.msa.coreui.components.CoreNavigationBar
import com.msa.coreui.components.CoreNavigationRail
import com.msa.coreui.components.CoreNavigationItem
import com.msa.coreui.components.CorePasswordField
import com.msa.coreui.components.CoreRadioRow
import com.msa.coreui.components.CoreSectionHeader
import com.msa.coreui.components.CoreSelectField
import com.msa.coreui.components.CoreSelectOption
import com.msa.coreui.components.CoreSnackbar
import com.msa.coreui.components.CoreSnackbarContent
import com.msa.coreui.components.CoreStyledCard
import com.msa.coreui.components.CoreSwitchRow
import com.msa.coreui.components.CoreTabItem
import com.msa.coreui.components.CoreTabs
import com.msa.coreui.components.CoreTextField
import com.msa.coreui.foundation.CoreContentContainer
import com.msa.coreui.foundation.CoreUiDarkColorScheme
import com.msa.coreui.foundation.CoreUiLightColorScheme
import com.msa.coreui.foundation.CoreUiMotion
import com.msa.coreui.foundation.CoreUiStrings
import com.msa.coreui.foundation.CoreUiTheme
import java.time.LocalDate

private val HeaderPurple = Color(0xFF6D45F5)
private val HeaderPurpleDeep = Color(0xFF4E2DD4)

private enum class CatalogPage {
    HOME,
    COMPONENTS,
    CALENDAR,
    SETTINGS,
    NAVIGATION,
    LAYOUT,
}

private enum class ComponentTab {
    INPUTS,
    FEEDBACK,
}

private data class CategoryItem(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val descriptionFa: String,
    val descriptionEn: String,
    val icon: ImageVector,
    val tint: Color,
    val destination: CatalogPage,
    val componentTab: ComponentTab? = null,
)

@Composable
fun CatalogRoot() {
    val context = LocalContext.current
    val preferences = remember(context.applicationContext) { CatalogPreferences(context) }
    CatalogRoot(preferences)
}

@Composable
internal fun CatalogRoot(settingsStorage: CatalogSettingsStorage) {
    val initialSettings = remember(settingsStorage) { settingsStorage.load() }

    var themeMode by rememberSaveable { mutableStateOf(initialSettings.themeMode) }
    var languageMode by rememberSaveable { mutableStateOf(initialSettings.languageMode) }
    var animationsEnabled by rememberSaveable { mutableStateOf(initialSettings.animationsEnabled) }
    var persianDigits by rememberSaveable { mutableStateOf(initialSettings.persianDigits) }
    var accent by rememberSaveable { mutableStateOf(initialSettings.accent) }

    val systemDarkTheme = isSystemInDarkTheme()
    val systemLanguageIsPersian = LocalConfiguration.current.locales[0].language.equals("fa", ignoreCase = true)
    val darkTheme = when (themeMode) {
        CatalogThemeMode.SYSTEM -> systemDarkTheme
        CatalogThemeMode.LIGHT -> false
        CatalogThemeMode.DARK -> true
    }
    val rtlMode = when (languageMode) {
        CatalogLanguageMode.SYSTEM -> systemLanguageIsPersian
        CatalogLanguageMode.PERSIAN -> true
        CatalogLanguageMode.ENGLISH -> false
    }

    LaunchedEffect(themeMode, languageMode, animationsEnabled, persianDigits, accent) {
        settingsStorage.save(
            CatalogSettings(
                themeMode = themeMode,
                languageMode = languageMode,
                animationsEnabled = animationsEnabled,
                persianDigits = persianDigits,
                accent = accent,
            ),
        )
    }

    val lightScheme = CoreUiLightColorScheme.copy(
        primary = accent.primary,
        onPrimary = Color.White,
        primaryContainer = accent.primaryContainer,
        onPrimaryContainer = Color(0xFF211347),
    )
    val darkScheme = CoreUiDarkColorScheme.copy(
        primary = accent.darkPrimary,
        onPrimary = Color(0xFF24105A),
        primaryContainer = accent.darkPrimaryContainer,
        onPrimaryContainer = Color.White,
    )

    CoreUiTheme(
        darkTheme = darkTheme,
        lightColorScheme = lightScheme,
        darkColorScheme = darkScheme,
        motion = CoreUiMotion(animationsEnabled = animationsEnabled),
        strings = if (rtlMode) CoreUiStrings.Persian else CoreUiStrings.English,
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (rtlMode) LayoutDirection.Rtl else LayoutDirection.Ltr,
        ) {
            CatalogApp(
                darkTheme = darkTheme,
                themeMode = themeMode,
                languageMode = languageMode,
                animationsEnabled = animationsEnabled,
                rtlMode = rtlMode,
                persianDigits = persianDigits,
                accent = accent,
                onThemeModeChange = { themeMode = it },
                onLanguageModeChange = { languageMode = it },
                onAnimationsEnabledChange = { animationsEnabled = it },
                onPersianDigitsChange = { persianDigits = it },
                onAccentChange = { accent = it },
                onResetSettings = {
                    val defaults = CatalogSettings()
                    settingsStorage.clear()
                    themeMode = defaults.themeMode
                    languageMode = defaults.languageMode
                    animationsEnabled = defaults.animationsEnabled
                    persianDigits = defaults.persianDigits
                    accent = defaults.accent
                },
            )
        }
    }
}

@Composable
private fun CatalogApp(
    darkTheme: Boolean,
    themeMode: CatalogThemeMode,
    languageMode: CatalogLanguageMode,
    animationsEnabled: Boolean,
    rtlMode: Boolean,
    persianDigits: Boolean,
    accent: CatalogAccent,
    onThemeModeChange: (CatalogThemeMode) -> Unit,
    onLanguageModeChange: (CatalogLanguageMode) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    onPersianDigitsChange: (Boolean) -> Unit,
    onAccentChange: (CatalogAccent) -> Unit,
    onResetSettings: () -> Unit,
) {
    var page by rememberSaveable { mutableStateOf(CatalogPage.HOME) }
    var componentTab by rememberSaveable { mutableStateOf(ComponentTab.INPUTS) }

    val primaryPages = listOf(
        CatalogPage.HOME,
        CatalogPage.COMPONENTS,
        CatalogPage.CALENDAR,
        CatalogPage.SETTINGS,
    )
    val navigationItems = listOf(
        CoreNavigationItem(tr(rtlMode, "خانه", "Home")),
        CoreNavigationItem(tr(rtlMode, "کامپوننت‌ها", "Components")),
        CoreNavigationItem(tr(rtlMode, "تقویم", "Calendar")),
        CoreNavigationItem(tr(rtlMode, "تنظیمات", "Settings")),
    )
    val selectedNavigationIndex = primaryPages.indexOf(page).takeIf { it >= 0 } ?: 0
    val onNavigationSelected: (Int) -> Unit = { index -> page = primaryPages[index] }
    val navigationIcon: @Composable (CoreNavigationItem, Boolean) -> Unit = { item, _ ->
        CatalogNavigationIcon(navigationItems, item)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth > CoreUiTheme.breakpoints.compactMaxWidth
        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail) {
                CoreNavigationRail(
                    items = navigationItems,
                    selectedIndex = selectedNavigationIndex,
                    onSelected = onNavigationSelected,
                    icon = navigationIcon,
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    header = { CatalogRailHeader() },
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            }

            Scaffold(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    CatalogTopBar(
                        page = page,
                        componentTab = componentTab,
                        rtlMode = rtlMode,
                        darkTheme = darkTheme,
                        onThemeToggle = {
                            onThemeModeChange(
                                if (darkTheme) CatalogThemeMode.LIGHT else CatalogThemeMode.DARK,
                            )
                        },
                        onSettingsClick = { page = CatalogPage.SETTINGS },
                        onBackClick = { page = CatalogPage.HOME },
                    )
                },
                bottomBar = {
                    if (!useNavigationRail) {
                        CoreNavigationBar(
                            items = navigationItems,
                            selectedIndex = selectedNavigationIndex,
                            onSelected = onNavigationSelected,
                            icon = navigationIcon,
                            modifier = Modifier.navigationBarsPadding(),
                            containerColor = MaterialTheme.colorScheme.surface,
                        )
                    }
                },
            ) { paddingValues ->
                when (page) {
                    CatalogPage.HOME -> HomePage(
                        modifier = Modifier.padding(paddingValues),
                        rtlMode = rtlMode,
                        onNavigate = { destination, tab ->
                            tab?.let { componentTab = it }
                            page = destination
                        },
                    )
                    CatalogPage.COMPONENTS -> ComponentsPage(
                        modifier = Modifier.padding(paddingValues),
                        rtlMode = rtlMode,
                        selectedTab = componentTab,
                        onTabChange = { componentTab = it },
                    )
                    CatalogPage.CALENDAR -> CalendarPage(
                        modifier = Modifier.padding(paddingValues),
                        rtlMode = rtlMode,
                        persianDigits = persianDigits,
                    )
                    CatalogPage.SETTINGS -> SettingsPage(
                        modifier = Modifier.padding(paddingValues),
                        rtlMode = rtlMode,
                        darkTheme = darkTheme,
                        themeMode = themeMode,
                        languageMode = languageMode,
                        animationsEnabled = animationsEnabled,
                        persianDigits = persianDigits,
                        accent = accent,
                        onThemeModeChange = onThemeModeChange,
                        onLanguageModeChange = onLanguageModeChange,
                        onAnimationsEnabledChange = onAnimationsEnabledChange,
                        onPersianDigitsChange = onPersianDigitsChange,
                        onAccentChange = onAccentChange,
                        onResetSettings = onResetSettings,
                    )
                    CatalogPage.NAVIGATION -> NavigationShowcasePage(
                        modifier = Modifier.padding(paddingValues),
                        rtlMode = rtlMode,
                    )
                    CatalogPage.LAYOUT -> LayoutShowcasePage(
                        modifier = Modifier.padding(paddingValues),
                        rtlMode = rtlMode,
                    )
                }
            }
        }
    }
}

@Composable
private fun CatalogNavigationIcon(
    navigationItems: List<CoreNavigationItem>,
    item: CoreNavigationItem,
) {
    val iconIndex = navigationItems.indexOf(item)
    val icon = listOf(
        CoreIcons.Home,
        CoreIcons.Menu,
        CoreIcons.Calendar,
        CoreIcons.Settings,
    ).getOrElse(iconIndex) { CoreIcons.Home }
    CoreIcon(
        imageVector = icon,
        contentDescription = null,
        decorative = true,
    )
}

@Composable
private fun CatalogRailHeader() {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .size(48.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "M",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun CatalogTopBar(
    page: CatalogPage,
    componentTab: ComponentTab,
    rtlMode: Boolean,
    darkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    if (page == CatalogPage.HOME) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(HeaderPurple, HeaderPurpleDeep),
                    ),
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = Color.White.copy(alpha = 0.16f),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = CoreUi.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = tr(rtlMode, "کاتالوگ کامپوننت‌ها", "Component Catalog"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.82f),
                    )
                }
                CoreIconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.testTag(CatalogTestTags.HEADER_SETTINGS),
                    contentDescription = tr(rtlMode, "بازکردن تنظیمات", "Open settings"),
                ) {
                    CoreIcon(
                        imageVector = CoreIcons.Settings,
                        contentDescription = null,
                        decorative = true,
                        tint = Color.White,
                    )
                }
            }
        }
    } else {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (page == CatalogPage.NAVIGATION || page == CatalogPage.LAYOUT) {
                    CoreIconButton(
                        onClick = onBackClick,
                        contentDescription = tr(rtlMode, "بازگشت به خانه", "Back to home"),
                    ) {
                        CoreIcon(
                            imageVector = if (rtlMode) CoreIcons.ChevronRight else CoreIcons.ArrowBack,
                            contentDescription = null,
                            decorative = true,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (page) {
                            CatalogPage.COMPONENTS -> when (componentTab) {
                                ComponentTab.INPUTS -> tr(rtlMode, "ورودی‌ها", "Inputs")
                                ComponentTab.FEEDBACK -> tr(rtlMode, "بازخوردها", "Feedback")
                            }
                            CatalogPage.CALENDAR -> tr(rtlMode, "انتخاب تاریخ", "Date selection")
                            CatalogPage.SETTINGS -> tr(rtlMode, "تم و تنظیمات", "Theme and settings")
                            CatalogPage.NAVIGATION -> tr(rtlMode, "ناوبری", "Navigation")
                            CatalogPage.LAYOUT -> tr(rtlMode, "چیدمان واکنش‌گرا", "Responsive layout")
                            CatalogPage.HOME -> CoreUi.name
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${CoreUi.name} · ${CoreUi.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                CoreIconButton(
                    onClick = onThemeToggle,
                    contentDescription = tr(rtlMode, "تغییر حالت نمایش", "Toggle appearance"),
                ) {
                    CoreIcon(
                        imageVector = if (darkTheme) CoreIcons.Visibility else CoreIcons.VisibilityOff,
                        contentDescription = null,
                        decorative = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomePage(
    modifier: Modifier,
    rtlMode: Boolean,
    onNavigate: (CatalogPage, ComponentTab?) -> Unit,
) {
    val categories = listOf(
        CategoryItem("foundation", "Foundation", "Foundation", "رنگ‌ها، تایپوگرافی و شکل‌ها", "Colors, typography and shapes", CoreIcons.Info, Color(0xFF7C4DFF), CatalogPage.SETTINGS),
        CategoryItem("inputs", "ورودی‌ها", "Inputs", "TextField، Select و Password", "Text fields, select and password", CoreIcons.Search, Color(0xFFEF6C9C), CatalogPage.COMPONENTS, ComponentTab.INPUTS),
        CategoryItem("selection", "انتخاب", "Selection", "Chip، Switch و کنترل‌های انتخاب", "Chips, switches and selection", CoreIcons.Check, Color(0xFF2A7DE1), CatalogPage.COMPONENTS, ComponentTab.INPUTS),
        CategoryItem("feedback", "بازخورد", "Feedback", "پیام‌ها، Snackbar و Dialog", "Banners, snackbars and dialogs", CoreIcons.Warning, Color(0xFFE84B6A), CatalogPage.COMPONENTS, ComponentTab.FEEDBACK),
        CategoryItem("navigation", "ناوبری", "Navigation", "Bottom bar و ساختار صفحات", "Bottom bar and screen structure", CoreIcons.Menu, Color(0xFF00A7B5), CatalogPage.NAVIGATION),
        CategoryItem("layout", "چیدمان", "Layout", "Scaffold، Grid و طراحی واکنش‌گرا", "Scaffold, grid and responsive layout", CoreIcons.Home, Color(0xFF168A76), CatalogPage.LAYOUT),
        CategoryItem("advanced", "پیشرفته", "Advanced", "تقویم، تم و دسترس‌پذیری", "Calendar, theme and accessibility", CoreIcons.Settings, Color(0xFF5A43E6), CatalogPage.CALENDAR),
    )

    CoreContentContainer(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag(CatalogTestTags.HOME_SCREEN),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                HeroSummary(rtlMode)
            }
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "دسته‌بندی‌ها", "Categories"),
                    supportingText = tr(rtlMode, "برای مشاهده نمونه‌های واقعی وارد هر بخش شوید", "Open a section to explore real component states"),
                )
            }
            items(categories, key = CategoryItem::id) { category ->
                CategoryCard(
                    item = category,
                    rtlMode = rtlMode,
                    onClick = { onNavigate(category.destination, category.componentTab) },
                )
            }
            item {
                CoreBanner(
                    title = tr(rtlMode, "آماده برای محصول واقعی", "Ready for real products"),
                    message = tr(
                        rtlMode,
                        "تمام صفحات از کامپوننت‌های خود کتابخانه استفاده می‌کنند و RTL، حالت تاریک و دسترس‌پذیری را پشتیبانی می‌کنند.",
                        "Every screen is built from the library itself with RTL, dark mode and accessibility support.",
                    ),
                    style = CoreBannerStyle.INFO,
                    icon = {
                        CoreIcon(CoreIcons.Info, null, decorative = true)
                    },
                )
            }
        }
    }
}

@Composable
private fun HeroSummary(rtlMode: Boolean) {
    CoreStyledCard(
        modifier = Modifier.fillMaxWidth(),
        style = CoreCardStyle.ELEVATED,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoreAvatar(
                    label = "MSA Core UI",
                    status = CoreAvatarStatus.ONLINE,
                    statusContentDescription = tr(rtlMode, "فعال", "Active"),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tr(rtlMode, "سیستم طراحی بومی اندروید", "Native Android design system"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = tr(rtlMode, "Material 3 · فارسی · RTL · تقویم شمسی", "Material 3 · Persian · RTL · Jalali calendar"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.74f),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CoreChip("Kotlin", onClick = {}, style = CoreChipStyle.INFO)
                CoreChip("Compose", onClick = {}, style = CoreChipStyle.SUCCESS)
                CoreChip("RTL", onClick = {}, style = CoreChipStyle.WARNING)
                CoreChip("A11y", onClick = {}, style = CoreChipStyle.INFO)
            }
        }
    }
}

@Composable
private fun CategoryCard(
    item: CategoryItem,
    rtlMode: Boolean,
    onClick: () -> Unit,
) {
    CoreStyledCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CatalogTestTags.category(item.id)),
        onClick = onClick,
        style = CoreCardStyle.ELEVATED,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = item.tint.copy(alpha = 0.14f),
                contentColor = item.tint,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CoreIcon(item.icon, null, decorative = true, tint = item.tint)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tr(rtlMode, item.titleFa, item.titleEn),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = tr(rtlMode, item.descriptionFa, item.descriptionEn),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            CoreIcon(
                imageVector = if (rtlMode) CoreIcons.ChevronLeft else CoreIcons.ChevronRight,
                contentDescription = null,
                decorative = true,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ComponentsPage(
    modifier: Modifier,
    rtlMode: Boolean,
    selectedTab: ComponentTab,
    onTabChange: (ComponentTab) -> Unit,
) {
    val tabs = listOf(
        CoreTabItem(tr(rtlMode, "ورودی‌ها", "Inputs")),
        CoreTabItem(tr(rtlMode, "بازخوردها", "Feedback")),
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(CatalogTestTags.COMPONENTS_SCREEN),
    ) {
        CoreTabs(
            items = tabs,
            selectedIndex = selectedTab.ordinal,
            onSelected = { onTabChange(ComponentTab.entries[it]) },
        )
        when (selectedTab) {
            ComponentTab.INPUTS -> InputsShowcase(rtlMode)
            ComponentTab.FEEDBACK -> FeedbackShowcase(rtlMode)
        }
    }
}

@Composable
private fun InputsShowcase(rtlMode: Boolean) {
    var simpleText by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("example@email.com") }
    var password by remember { mutableStateOf("MSA-Core-UI") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf("second") }
    var selectedValues by rememberSaveable { mutableStateOf(listOf("first", "third")) }
    var notes by rememberSaveable { mutableStateOf("") }
    var saved by rememberSaveable { mutableStateOf(false) }

    CoreContentContainer(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "فرم‌ها و ورودی‌ها", "Forms and inputs"),
                    supportingText = tr(rtlMode, "نمونه‌ی کامل فرم با validation و state کنترل‌شده", "A complete controlled form with validation"),
                )
            }
            item {
                CoreCard(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CoreTextField(
                            value = simpleText,
                            onValueChange = { simpleText = it },
                            label = tr(rtlMode, "متن ساده", "Simple text"),
                            placeholder = tr(rtlMode, "متن دلخواه خود را وارد کنید", "Enter any text"),
                            required = true,
                            maxLength = 60,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CoreTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = tr(rtlMode, "ایمیل", "Email"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            status = if (email.contains('@')) CoreFieldStatus.SUCCESS else CoreFieldStatus.ERROR,
                            supportingText = if (email.contains('@')) {
                                tr(rtlMode, "فرمت ایمیل معتبر است", "Email format is valid")
                            } else {
                                tr(rtlMode, "ایمیل معتبر وارد کنید", "Enter a valid email")
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CorePasswordField(
                            value = password,
                            onValueChange = { password = it },
                            label = tr(rtlMode, "رمز عبور", "Password"),
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityChange = { passwordVisible = it },
                            supportingText = tr(rtlMode, "حداقل ۸ نویسه", "At least 8 characters"),
                            status = if (password.length >= 8) CoreFieldStatus.SUCCESS else CoreFieldStatus.WARNING,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CoreSelectField(
                            options = listOf(
                                CoreSelectOption("first", tr(rtlMode, "گزینه اول", "First option")),
                                CoreSelectOption("second", tr(rtlMode, "گزینه دوم", "Second option")),
                                CoreSelectOption("third", tr(rtlMode, "گزینه سوم", "Third option")),
                            ),
                            selectedValue = selectedOption,
                            onValueSelected = { selectedOption = it },
                            label = tr(rtlMode, "انتخاب تک‌گزینه", "Single select"),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CoreMultiSelectField(
                            options = listOf(
                                CoreSelectOption("first", tr(rtlMode, "گزینه اول", "First option")),
                                CoreSelectOption("second", tr(rtlMode, "گزینه دوم", "Second option")),
                                CoreSelectOption("third", tr(rtlMode, "گزینه سوم", "Third option")),
                                CoreSelectOption("fourth", tr(rtlMode, "گزینه چهارم", "Fourth option")),
                            ),
                            selectedValues = selectedValues.toSet(),
                            onSelectionChange = { selectedValues = it.toList() },
                            label = tr(rtlMode, "انتخاب چندگزینه", "Multi select"),
                            maxSelection = 3,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CoreTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = tr(rtlMode, "توضیحات", "Notes"),
                            singleLine = false,
                            minLines = 3,
                            maxLines = 5,
                            maxLength = 240,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        CoreButton(
                            content = CoreButtonContent(tr(rtlMode, "ذخیره", "Save")),
                            onClick = { saved = true },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                CoreIcon(CoreIcons.Check, null, decorative = true)
                            },
                        )
                    }
                }
            }
            if (saved) {
                item {
                    CoreBanner(
                        title = tr(rtlMode, "اطلاعات ذخیره شد", "Saved successfully"),
                        message = tr(rtlMode, "فرم با موفقیت اعتبارسنجی و ذخیره شد.", "The form was validated and saved."),
                        style = CoreBannerStyle.SUCCESS,
                        dismissLabel = tr(rtlMode, "بستن", "Dismiss"),
                        onDismiss = { saved = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackShowcase(rtlMode: Boolean) {
    var dialogVisible by remember { mutableStateOf(false) }
    var snackbarVisible by remember { mutableStateOf(true) }

    CoreContentContainer(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "پیام‌ها و بازخوردها", "Messages and feedback"),
                    supportingText = tr(rtlMode, "وضعیت‌های موفقیت، هشدار، خطا و تایید", "Success, warning, error and confirmation states"),
                )
            }
            item {
                CoreBanner(
                    title = tr(rtlMode, "عملیات با موفقیت انجام شد", "Operation completed"),
                    message = tr(rtlMode, "تغییرات شما ذخیره گردید.", "Your changes were saved."),
                    style = CoreBannerStyle.SUCCESS,
                    icon = { CoreIcon(CoreIcons.Check, null, decorative = true) },
                )
            }
            item {
                CoreBanner(
                    title = tr(rtlMode, "هشدار", "Warning"),
                    message = tr(rtlMode, "لطفاً اطلاعات را با دقت وارد کنید.", "Please review the information carefully."),
                    style = CoreBannerStyle.WARNING,
                    icon = { CoreIcon(CoreIcons.Warning, null, decorative = true) },
                )
            }
            item {
                CoreBanner(
                    title = tr(rtlMode, "خطا", "Error"),
                    message = tr(rtlMode, "عملیات با خطا مواجه شد.", "The operation could not be completed."),
                    style = CoreBannerStyle.ERROR,
                    icon = { CoreIcon(CoreIcons.Close, null, decorative = true) },
                )
            }
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "Snackbar", "Snackbar"),
                    supportingText = tr(rtlMode, "پیام کوتاه با اکشن اختیاری", "Brief feedback with an optional action"),
                )
            }
            if (snackbarVisible) {
                item {
                    CoreSnackbar(
                        content = CoreSnackbarContent(
                            message = tr(rtlMode, "اطلاعات با موفقیت کپی شد", "Information copied"),
                            actionLabel = tr(rtlMode, "بستن", "Close"),
                            style = CoreBannerStyle.INFO,
                        ),
                        onAction = { snackbarVisible = false },
                    )
                }
            } else {
                item {
                    CoreButton(
                        content = CoreButtonContent(tr(rtlMode, "نمایش Snackbar", "Show snackbar")),
                        onClick = { snackbarVisible = true },
                        style = CoreButtonStyle.SECONDARY,
                    )
                }
            }
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "دیالوگ تایید", "Confirmation dialog"),
                    supportingText = tr(rtlMode, "تایید امن پیش از عملیات حساس", "Safe confirmation before sensitive actions"),
                )
            }
            item {
                CoreCard(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = tr(rtlMode, "آیا از حذف این مورد مطمئن هستید؟", "Are you sure you want to delete this item?"),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        CoreButton(
                            content = CoreButtonContent(tr(rtlMode, "بازکردن دیالوگ", "Open dialog")),
                            onClick = { dialogVisible = true },
                            style = CoreButtonStyle.DESTRUCTIVE,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    if (dialogVisible) {
        CoreAlertDialog(
            title = tr(rtlMode, "تایید حذف", "Confirm deletion"),
            message = tr(rtlMode, "این عملیات قابل بازگشت نیست.", "This action cannot be undone."),
            confirmLabel = tr(rtlMode, "حذف", "Delete"),
            dismissLabel = tr(rtlMode, "لغو", "Cancel"),
            destructive = true,
            onConfirm = { dialogVisible = false },
            onDismissRequest = { dialogVisible = false },
            icon = { CoreIcon(CoreIcons.Warning, null, decorative = true) },
        )
    }
}

@Composable
private fun CalendarPage(
    modifier: Modifier,
    rtlMode: Boolean,
    persianDigits: Boolean,
) {
    var selectionMode by rememberSaveable { mutableStateOf(PersianDateSelectionMode.RANGE) }
    var confirmedSelection by remember { mutableStateOf<PersianDateSelection?>(null) }
    val config = PersianDatePickerConfig(
        minDate = LocalDate.of(2000, 1, 1),
        maxDate = LocalDate.of(2035, 12, 31),
        minRangeDays = if (selectionMode == PersianDateSelectionMode.RANGE) 2 else 1,
        maxRangeDays = if (selectionMode == PersianDateSelectionMode.RANGE) 30 else null,
        minMultipleSelection = if (selectionMode == PersianDateSelectionMode.MULTIPLE) 1 else 0,
        maxMultipleSelection = if (selectionMode == PersianDateSelectionMode.MULTIPLE) 5 else null,
        digitStyle = if (persianDigits) PersianDateDigitStyle.PERSIAN else PersianDateDigitStyle.LATIN,
    )
    val state = rememberPersianDatePickerState(
        selectionMode = selectionMode,
        initialSelection = PersianDateSelection.empty(selectionMode),
        config = config,
    )
    val strings = if (rtlMode) PersianDatePickerStrings.Persian else PersianDatePickerStrings.English

    CoreContentContainer(
        modifier = modifier
            .fillMaxSize()
            .testTag(CatalogTestTags.CALENDAR_SCREEN),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PersianDateSelectionMode.entries.forEach { mode ->
                        CoreChip(
                            label = when (mode) {
                                PersianDateSelectionMode.SINGLE -> tr(rtlMode, "تک‌تاریخ", "Single")
                                PersianDateSelectionMode.RANGE -> tr(rtlMode, "بازه", "Range")
                                PersianDateSelectionMode.MULTIPLE -> tr(rtlMode, "چندتایی", "Multiple")
                            },
                            onClick = {
                                selectionMode = mode
                                confirmedSelection = null
                            },
                            selected = selectionMode == mode,
                        )
                    }
                }
            }
            item {
                PersianDatePicker(
                    state = state,
                    strings = strings,
                    modifier = Modifier.fillMaxWidth(),
                    dateIndicatorCount = { date -> if (date.dayOfMonth % 7 == 0) 1 else 0 },
                    dateAdditionalDescription = { date ->
                        if (date.dayOfMonth % 7 == 0) tr(rtlMode, "دارای رویداد", "Has an event") else null
                    },
                )
            }
            item {
                CoreButton(
                    content = CoreButtonContent(tr(rtlMode, "تایید انتخاب", "Confirm selection")),
                    onClick = { confirmedSelection = state.selection },
                    enabled = state.canConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { CoreIcon(CoreIcons.Check, null, decorative = true) },
                )
            }
            confirmedSelection?.let { selection ->
                item {
                    CoreBanner(
                        title = tr(rtlMode, "انتخاب ثبت شد", "Selection confirmed"),
                        message = PersianDateSelectionFormatter.format(
                            selection = selection,
                            strings = strings,
                            usePersianDigits = persianDigits,
                            emptyText = strings.noSelection,
                        ),
                        style = CoreBannerStyle.SUCCESS,
                        dismissLabel = tr(rtlMode, "پاک‌کردن", "Clear"),
                        onDismiss = { confirmedSelection = null },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsPage(
    modifier: Modifier,
    rtlMode: Boolean,
    darkTheme: Boolean,
    themeMode: CatalogThemeMode,
    languageMode: CatalogLanguageMode,
    animationsEnabled: Boolean,
    persianDigits: Boolean,
    accent: CatalogAccent,
    onThemeModeChange: (CatalogThemeMode) -> Unit,
    onLanguageModeChange: (CatalogLanguageMode) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    onPersianDigitsChange: (Boolean) -> Unit,
    onAccentChange: (CatalogAccent) -> Unit,
    onResetSettings: () -> Unit,
) {
    CoreContentContainer(
        modifier = modifier
            .fillMaxSize()
            .testTag(CatalogTestTags.SETTINGS_SCREEN),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "حالت نمایش", "Appearance"),
                    supportingText = tr(
                        rtlMode,
                        "تنظیمات ذخیره می‌شوند و پس از بازکردن دوباره‌ی برنامه باقی می‌مانند",
                        "Preferences are persisted and restored when the app opens again",
                    ),
                )
            }
            item {
                CoreCard(Modifier.fillMaxWidth()) {
                    Column {
                        CoreRadioRow(
                            title = tr(rtlMode, "پیش‌فرض سیستم", "System default"),
                            selected = themeMode == CatalogThemeMode.SYSTEM,
                            onClick = { onThemeModeChange(CatalogThemeMode.SYSTEM) },
                            supportingText = tr(
                                rtlMode,
                                "هماهنگ با حالت روشن یا تاریک دستگاه",
                                "Follow the device light or dark appearance",
                            ),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        CoreRadioRow(
                            title = tr(rtlMode, "حالت روشن", "Light mode"),
                            selected = themeMode == CatalogThemeMode.LIGHT,
                            onClick = { onThemeModeChange(CatalogThemeMode.LIGHT) },
                            supportingText = tr(rtlMode, "پس‌زمینه روشن و کنتراست استاندارد", "Bright surfaces and standard contrast"),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        CoreRadioRow(
                            title = tr(rtlMode, "حالت تاریک", "Dark mode"),
                            selected = themeMode == CatalogThemeMode.DARK,
                            onClick = { onThemeModeChange(CatalogThemeMode.DARK) },
                            supportingText = tr(rtlMode, "مناسب محیط کم‌نور", "Optimized for low-light environments"),
                        )
                    }
                }
            }
            item {
                CoreBanner(
                    title = tr(rtlMode, "حالت فعال", "Active appearance"),
                    message = if (darkTheme) {
                        tr(rtlMode, "در حال نمایش تم تاریک", "Dark theme is currently active")
                    } else {
                        tr(rtlMode, "در حال نمایش تم روشن", "Light theme is currently active")
                    },
                    style = CoreBannerStyle.INFO,
                )
            }
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "رنگ اصلی", "Primary color"),
                    supportingText = tr(rtlMode, "پالت برند را انتخاب کنید", "Choose a brand palette"),
                )
            }
            item {
                CoreCard(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CatalogAccent.entries.forEach { item ->
                            AccentSwatch(
                                id = item.name.lowercase(),
                                color = item.primary,
                                selected = accent == item,
                                onClick = { onAccentChange(item) },
                                contentDescription = tr(
                                    rtlMode,
                                    "انتخاب رنگ ${item.name.lowercase()}",
                                    "Select ${item.name.lowercase()} accent",
                                ),
                            )
                        }
                    }
                }
            }
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "زبان و جهت", "Language and direction"),
                    supportingText = tr(
                        rtlMode,
                        "فارسی راست‌به‌چپ، انگلیسی چپ‌به‌راست یا زبان سیستم",
                        "Persian RTL, English LTR, or the system language",
                    ),
                )
            }
            item {
                CoreCard(Modifier.fillMaxWidth()) {
                    Column {
                        CoreRadioRow(
                            title = tr(rtlMode, "پیش‌فرض سیستم", "System default"),
                            selected = languageMode == CatalogLanguageMode.SYSTEM,
                            onClick = { onLanguageModeChange(CatalogLanguageMode.SYSTEM) },
                            supportingText = tr(rtlMode, "استفاده از زبان دستگاه", "Use the device language"),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        CoreRadioRow(
                            title = "فارسی",
                            selected = languageMode == CatalogLanguageMode.PERSIAN,
                            onClick = { onLanguageModeChange(CatalogLanguageMode.PERSIAN) },
                            supportingText = "رابط راست‌به‌چپ",
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        CoreRadioRow(
                            title = "English",
                            selected = languageMode == CatalogLanguageMode.ENGLISH,
                            onClick = { onLanguageModeChange(CatalogLanguageMode.ENGLISH) },
                            supportingText = "Left-to-right interface",
                        )
                    }
                }
            }
            item {
                CoreSectionHeader(
                    title = tr(rtlMode, "اعداد و حرکت", "Numbers and motion"),
                    supportingText = tr(rtlMode, "نمایش ارقام و سیاست کاهش حرکت", "Digit presentation and reduced-motion policy"),
                )
            }
            item {
                CoreCard(Modifier.fillMaxWidth()) {
                    Column {
                        CoreSwitchRow(
                            title = tr(rtlMode, "اعداد فارسی", "Persian digits"),
                            supportingText = if (persianDigits) "۱۲۳۴۵۶۷۸۹۰" else "1234567890",
                            checked = persianDigits,
                            onCheckedChange = onPersianDigitsChange,
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        CoreSwitchRow(
                            title = tr(rtlMode, "حرکت و انیمیشن", "Motion and animation"),
                            supportingText = tr(rtlMode, "برای Reduce Motion غیرفعال کنید", "Disable to preview reduced motion"),
                            checked = animationsEnabled,
                            onCheckedChange = onAnimationsEnabledChange,
                        )
                    }
                }
            }
            item {
                CoreBanner(
                    title = tr(rtlMode, "دسترس‌پذیری فعال", "Accessibility enabled"),
                    message = tr(
                        rtlMode,
                        "حداقل سطح لمس ۴۸dp، semantic role، وضعیت انتخاب و پیام‌های TalkBack در کامپوننت‌ها رعایت شده است.",
                        "48dp touch targets, semantic roles, selection states and TalkBack messages are built in.",
                    ),
                    style = CoreBannerStyle.INFO,
                    icon = { CoreIcon(CoreIcons.Info, null, decorative = true) },
                )
            }
            item {
                CoreButton(
                    content = CoreButtonContent(tr(rtlMode, "بازنشانی تنظیمات", "Reset settings")),
                    onClick = onResetSettings,
                    style = CoreButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { CoreIcon(CoreIcons.Refresh, null, decorative = true) },
                )
            }
        }
    }
}

@Composable
private fun AccentSwatch(
    id: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .testTag("catalog.accent.$id")
            .semantics { this.contentDescription = contentDescription },
        shape = CircleShape,
        color = color,
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface)
        } else {
            null
        },
    ) {
        if (selected) {
            Box(contentAlignment = Alignment.Center) {
                CoreIcon(CoreIcons.Check, null, decorative = true, tint = Color.White)
            }
        }
    }
}

private fun tr(rtlMode: Boolean, fa: String, en: String): String = if (rtlMode) fa else en
