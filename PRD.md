# سند نیازمندی محصول (PRD) — MSA Core UI 5.0

## کنترل سند

| فیلد | مقدار |
|---|---|
| محصول | MSA Core UI |
| نسخه هدف | 5.6.0 |
| وضعیت | Release Candidate specification |
| پلتفرم | Android / Jetpack Compose |
| حداقل Android | API 26 |
| مالک فنی | ALISCHILLER — MSA |
| نوع محصول | کتابخانه Design System و Persian/Solar Hijri Calendar |
| مدل انتشار | ماژول‌های مستقل Maven + artifact تجمیعی + BOM |
| مجوز | Apache License 2.0 با Attributionهای ثبت‌شده |

## 1. خلاصه اجرایی

MSA Core UI یک کتابخانه چندماژوله برای تیم‌های Android است که دو نیاز را با یک قرارداد مهندسی واحد پوشش می‌دهد:

1. Foundation و مجموعه‌ای از Componentهای قابل‌استفاده مجدد بر پایه Material 3.
2. Calendar و Date Picker جلالی با مدل تاریخ type-safe، تبدیل deterministic و پشتیبانی واقعی از RTL و Accessibility.

نسخه 5.0 باید مصرف UI مشترک در چند اپلیکیشن را استاندارد کند، از تکرار Theme و Component جلوگیری کند، و استفاده از تاریخ جلالی را بدون Map، String parsing پراکنده یا وابستگی به Timezone امکان‌پذیر سازد.

این نسخه تا پیش از عبور از Debug/Release Build، Lint، Unit Test، Instrumented Test، Maven Consumer و Runtime smoke test با عنوان **Production-oriented Release Candidate** شناخته می‌شود، نه Production-Ready.

## 2. مسئله محصول

تیم‌های Android معمولاً با این مشکلات روبه‌رو هستند:

- تعریف چندباره رنگ، spacing، typography و stateهای UI در هر پروژه.
- رفتار متفاوت Button، Field، Dialog، Loading و Feedback میان Featureها.
- استفاده از APIهای غیر type-safe برای تاریخ جلالی.
- اختلاف میان تاریخ نمایش‌داده‌شده و تاریخ ذخیره‌شده.
- نبود قرارداد مشخص برای RTL، Persian digits، TalkBack و large font.
- وابستگی مصرف‌کننده به Moduleهای داخلی یا AAR بدون metadata.
- نبود مسیر انتشار، مهاجرت، rollback و تست Consumer.

## 3. چشم‌انداز

یک Design System کوچک، پایدار و قابل انتشار که:

- API عمومی کم‌حجم و قابل پیش‌بینی داشته باشد.
- رفتار و ظاهر Componentهای پایه را یکپارچه کند.
- Calendar جلالی را به‌صورت مستقل از UI و Timezone مدل کند.
- برای اپ فارسی، دوزبانه و RTL قابل استفاده باشد.
- با یک dependency یا انتخاب ماژول‌های مستقل نصب شود.
- انتشار آن با Maven metadata، BOM، Source artifact و Dokka قابل کنترل باشد.

## 4. کاربران هدف

### 4.1 توسعه‌دهنده Android

نیاز دارد Componentها را با API Compose استاندارد، `Modifier`, state hoisting و callbackهای روشن استفاده کند.

### 4.2 تیم Design System

نیاز دارد color، typography، spacing، shape، elevation، motion و semantic states را از یک Theme مرکزی کنترل کند.

### 4.3 توسعه‌دهنده Featureهای فارسی

نیاز دارد تاریخ جلالی را نمایش، انتخاب، تبدیل، parse و format کند، بدون اینکه Business Logic به Compose یا Android Context وابسته شود.

### 4.4 مهندس Release و Platform

نیاز دارد artifactها، POM، version alignment، consumer compilation، CI gate و rollback را قابل بررسی کند.

## 5. اهداف نسخه 5.0

| شناسه | هدف | معیار موفقیت |
|---|---|---|
| G-01 | یکپارچه‌سازی Foundation | تمام Componentهای عمومی از Tokenهای مرکزی استفاده کنند |
| G-02 | API پایدار Compose | تمام UI APIها `Modifier`، state ownership و callback روشن داشته باشند |
| G-03 | Calendar صحیح | Round-trip تبدیل تاریخ در کل بازه پشتیبانی‌شده بدون mismatch باشد |
| G-04 | Accessibility | Componentهای تعاملی semantics و touch target مناسب داشته باشند |
| G-05 | RTL و Localization | Date Picker در Persian/English و RTL/LTR قابل استفاده باشد |
| G-06 | انتشار ماژولار | Aggregate artifact، artifactهای مستقل و BOM منتشر شوند |
| G-07 | Verification مصرف‌کننده | Project consumer و Maven consumer هر دو Compile شوند |
| G-08 | Release governance | PRD، Changelog، Migration، Rollback و Release Checklist وجود داشته باشند |

## 6. Non-goals نسخه 5.0

- تبدیل پروژه به Kotlin Multiplatform یا Compose Multiplatform.
- ارائه Navigation framework، Networking، Persistence یا Dependency Injection.
- ذخیره خودکار تاریخ یا اتصال به Backend.
- تحمیل Font یا Brand asset اختصاصی به مصرف‌کننده.
- ارائه Date/Time Picker با ساعت و Timezone conversion.
- انتشار خودکار به Maven Central بدون Signing و credential مدیریت‌شده.
- جایگزینی تمام Componentهای Material 3.

## 7. محدوده محصول

### 7.1 Foundation

- Light/Dark Color Scheme
- Semantic status colors
- Typography و Shapes
- Spacing، Radius، Elevation، Alpha و Stroke
- Sizing و touch target
- Motion policy و reduced motion
- Breakpoint و adaptive content container
- Shared fallback/accessibility strings

### 7.2 Components

- Button و IconButton
- TextField با validation status
- Card و List Item
- Chip و Selection Controlها
- Banner، Snackbar و State View
- Dialog
- Determinate/Indeterminate Progress
- Loading indicator

### 7.3 Calendar Core

- `PersianDate` و `PersianYearMonth`
- Gregorian ↔ Solar Hijri conversion
- Validation و leap year
- Date arithmetic
- Persian/Arabic/Latin digit normalization
- Formatting و parsing
- Single/Range/Multiple selection models

### 7.4 Calendar UI

- Single، Range و Multiple selection
- min/max boundary
- disabled dates
- first day of week
- adjacent month days
- month/year selector
- injected `Clock`
- Persian/Latin digit style
- Persian/English strings
- RTL/LTR
- state restoration
- semantics برای selected، range، disabled، today و indicator

### 7.5 Distribution

- Artifact تجمیعی `core-ui`
- Artifactهای مستقل
- `core-ui-bom`
- Source JAR/AAR metadata
- Dokka
- Catalog
- Project consumer smoke test
- Maven metadata consumer test

## 8. نیازمندی‌های عملکردی

### 8.1 Theme و Tokenها

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| FR-001 | مصرف‌کننده باید Theme را در Root تزریق کند | Must | تمام Componentها زیر `CoreUiTheme` درست Render شوند |
| FR-002 | Token familyها باید قابل override باشند | Must | custom spacing، motion، sizing و strings از CompositionLocal خوانده شوند |
| FR-003 | Dynamic color باید opt-in باشد | Must | حالت پیش‌فرض Brand scheme ثابت بماند |
| FR-004 | reduced motion باید animationهای داخلی را غیرفعال کند | Must | با `animationsEnabled=false` transition داخلی اجرا نشود |
| FR-005 | Layout باید breakpoint فعال را گزارش کند | Should | compact/medium/expanded طبق token قابل تغییر محاسبه شود |

### 8.2 Componentها

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| FR-101 | Button باید style، size، enabled و loading داشته باشد | Must | loading کلیک را غیرفعال و progress semantics ایجاد کند |
| FR-102 | IconButton باید label اجباری داشته باشد | Must | label خالی با خطای روشن رد شود |
| FR-103 | TextField باید status semantic داشته باشد | Must | error/success/warning در supporting text و colors منعکس شود |
| FR-104 | Selection row باید کل ردیف را touch target کند | Must | Switch/Checkbox/Radio semantics duplicate نداشته باشند |
| FR-105 | Feedback باید action pair معتبر داشته باشد | Must | label بدون callback یا callback بدون label رد شود |
| FR-106 | Progress باید مقدار `0..1` یا indeterminate بپذیرد | Must | NaN و مقدار خارج بازه رد شوند |
| FR-107 | Componentهای public باید `modifier: Modifier = Modifier` داشته باشند | Must | API review و compilation consumer این قرارداد را تأیید کند |

### 8.3 Calendar Core

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| FR-201 | Conversion باید deterministic باشد | Must | Timezone و Locale دستگاه نتیجه conversion را تغییر ندهند |
| FR-202 | مدل تاریخ باید تاریخ نامعتبر را رد کند | Must | ماه/روز خارج بازه با `IllegalArgumentException` رد شود |
| FR-203 | Round-trip باید بدون mismatch باشد | Must | کل بازه پشتیبانی‌شده به Gregorian و مجدداً Persian برگردد |
| FR-204 | Parser باید ارقام فارسی، عربی و لاتین را بپذیرد | Must | ورودی معادل نتیجه یکسان دهد |
| FR-205 | API Core نباید Android/Compose dependency داشته باشد | Must | ماژول با Kotlin/JVM مستقل Compile شود |

### 8.4 Date Picker

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| FR-301 | Single selection | Must | انتخاب جدید مقدار قبلی را جایگزین کند |
| FR-302 | Range selection | Must | start/end مرتب و range ناقص قابل تشخیص باشد |
| FR-303 | Multiple selection | Must | toggle و maximum selection رعایت شود |
| FR-304 | Disabled date | Must | روز غیرفعال قابل انتخاب نباشد |
| FR-305 | Boundary | Must | navigation و selection از min/max خارج نشود |
| FR-306 | Range policy | Must | عبور از disabled date طبق config مجاز یا رد شود |
| FR-307 | State restoration | Must | selection، displayed month و view بازسازی شوند |
| FR-308 | Consumer-owned dismissal | Must | Confirm به‌صورت خودکار Dialog را نبندد |
| FR-309 | RTL | Must | ترتیب و navigation در RTL بدون استفاده از left/right صحیح باشد |
| FR-310 | Accessibility | Must | full date، selected، disabled، today و range role اعلام شوند |

### 8.5 انتشار

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| FR-401 | Aggregate artifact | Must | مصرف‌کننده فقط با `core-ui` Compile شود |
| FR-402 | Selective artifacts | Must | Foundation/Components/Calendar جدا قابل Resolve باشند |
| FR-403 | BOM | Must | version همه artifactها بدون تکرار در dependencyها align شود |
| FR-404 | Maven consumer | Must | مصرف‌کننده بدون `project(...)` از repository تولیدشده Compile شود |
| FR-405 | POM metadata | Must | License، Developer، SCM و dependency metadata وجود داشته باشد |
| FR-406 | Documentation | Must | README، API، Migration، Changelog، PRD و Release Checklist همراه release باشند |

## 9. نیازمندی‌های غیرعملکردی

### 9.1 قابلیت اطمینان

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| NFR-001 | مسیر Runtime نباید placeholder اجرایی داشته باشد | Must | اسکن Production source هیچ `TODO()`، `error("Not implemented")` یا `UnsupportedOperationException` قابل‌دسترسی پیدا نکند |
| NFR-002 | APIهای `try*` نباید خطاهای fatal را پنهان کنند | Must | فقط خطاهای ورودی قابل‌انتظار به نتیجه nullable تبدیل شوند و Errorهای JVM منتشر شوند |
| NFR-003 | Collectionهای ورودی عمومی باید defensive-copy شوند | Must | تغییر Collection مصرف‌کننده پس از ساخت مدل، State داخلی را تغییر ندهد |
| NFR-004 | Calendar Core باید thread-safe باشد | Must | mutable global state وجود نداشته باشد و conversion فقط به ورودی وابسته باشد |

### 9.2 کارایی

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| NFR-101 | Calendar Grid باید ساختار داده bounded داشته باشد | Must | ساخت یک ماه حداکثر تعداد ثابت cell تولید کند و loop نامحدود نداشته باشد |
| NFR-102 | Animation باید با policy مرکزی قابل خاموش‌کردن باشد | Must | با reduced-motion، transitionهای داخلی اجرا نشوند |
| NFR-103 | Componentها نباید I/O، Network یا blocking operation اجرا کنند | Must | dependency و source audit هیچ عملیات خارجی در UI library پیدا نکند |
| NFR-104 | ادعای Performance فقط باید مبتنی بر اندازه‌گیری باشد | Must | عدد startup، binary size، recomposition یا frame time فقط همراه benchmark/profiler evidence منتشر شود |

### 9.3 امنیت و حریم خصوصی

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| NFR-201 | Library نباید telemetry، network، permission یا storage خودکار داشته باشد | Must | Manifest و dependency audit فاقد permission و client ذخیره‌سازی/شبکه‌ی First-party باشد |
| NFR-202 | Secret و signing material نباید داخل Repository باشد | Must | filename و key-pattern scan بدون finding تأییدنشده پاس شود |
| NFR-203 | Workflowها باید از اصل حداقل دسترسی پیروی کنند | Must | permissionهای Workflow محدود و credential checkout غیرفعال باشد |
| NFR-204 | Release artifact باید به Version، Revision و Checksum قابل ردیابی باشد | Must | Release manifest دارای version و source revision باشد و checksum فایل‌های منتشرشده ثبت شود |

### 9.4 سازگاری

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| NFR-301 | حداقل نسخه Android باید API 26 باشد | Must | manifest merge و instrumented smoke روی API 26 پاس شود |
| NFR-302 | Toolchain رسمی باید JDK/JVM 17 باشد | Must | build و bytecode target با Java 17 انجام شود |
| NFR-303 | Public UI فقط Material 3 باشد | Must | import یا dependency تولیدی Material 2 وجود نداشته باشد |
| NFR-304 | Calendar Core باید از Java قابل مصرف باشد | Should | Java consumer APIهای عمومی اصلی را Compile کند |
| NFR-305 | Breaking change باید از SemVer و deprecation policy پیروی کند | Must | breaking API بدون Major version یا migration/deprecation مصوب رد شود |

### 9.5 دسترس‌پذیری

| ID | نیازمندی | اولویت | معیار پذیرش |
|---|---|---|---|
| NFR-401 | minimum touch target باید 48dp یا بیشتر باشد | Must | تست UI اندازه targetهای تعاملی اصلی را تأیید کند |
| NFR-402 | رنگ نباید تنها نشانه وضعیت باشد | Must | stateهای error/success/warning متن، icon یا semantics مکمل داشته باشند |
| NFR-403 | Actionهای icon-only باید label اجباری داشته باشند | Must | label خالی در API رد شود و TalkBack نام action را اعلام کند |
| NFR-404 | statusهای error/offline باید announcement مناسب داشته باشند | Must | semantics از live region مناسب استفاده کند |
| NFR-405 | UI باید با large font و عرض کوچک قابل استفاده بماند | Must | Catalog در font scale بزرگ و compact width بدون clipping یا action غیرقابل‌دسترسی بررسی شود |

## 10. معماری محصول

```text
core-ui-bom
    └── version constraints

core-ui-calendar-core (Pure JVM)
    ├── PersianDate / PersianYearMonth
    ├── conversion / arithmetic
    └── parsing / formatting

core-ui-foundation (Android Compose)
    ├── Theme / Color / Typography
    ├── Tokens / Motion / Sizing
    └── Adaptive layout / Strings

core-ui-components (Android Compose)
    └── depends on foundation

core-ui-calendar (Android Compose)
    └── depends on calendar-core + foundation + components

core-ui (Aggregate)
    └── re-exports all public modules
```

## 11. API و قرارداد داده

- Storage و API payload پیشنهادی: `java.time.LocalDate` یا ISO-8601.
- Presentation و arithmetic جلالی: `PersianDate`.
- Callback Date Picker: `PersianDateSelection` شامل Gregorian `LocalDate`.
- تبدیل ارقام فارسی فقط در Presentation انجام می‌شود.
- Calendar conversion از Timezone مستقل است.
- مفهوم «امروز» با `Clock` قابل تزریق محاسبه می‌شود.

## 12. تجربه مصرف‌کننده

### نصب ساده

```kotlin
dependencies {
    implementation("com.msa.coreui:core-ui:5.6.0")
}
```

### نصب انتخابی با BOM

```kotlin
dependencies {
    implementation(platform("com.msa.coreui:core-ui-bom:5.6.0"))
    implementation("com.msa.coreui:core-ui-foundation")
    implementation("com.msa.coreui:core-ui-components")
    implementation("com.msa.coreui:core-ui-calendar")
}
```

## 13. معیارهای پذیرش Release

### Gate A — Source و Static

- conflict marker صفر
- Runtime TODO صفر
- dynamic dependency صفر
- Secret pattern بدون مورد تأییدنشده
- XML/TOML/YAML معتبر
- License و Attribution موجود

### Gate B — Build

- Fresh dependency resolution
- Calendar core JVM compile/test
- Android Debug build
- Android minified Release build
- Lint Release برای تمام Libraryها
- Catalog Release build

### Gate C — Test

- Unit testهای Calendar core
- State و Contract testهای Date Picker
- Compose UI test برای Component و Calendar
- API 26 و API جدید در Emulator
- RTL، dark theme، large font و state restoration smoke test

### Gate D — Distribution

- Publication تمام artifactها و BOM
- POM inspection
- Maven consumer compile
- Source artifacts
- Dokka generation
- Checksum و release manifest

### Gate E — Runtime

- نصب Catalog Release APK
- بازکردن screenهای Component
- اجرای Single/Range/Multiple Date Picker
- TalkBack smoke test
- Minified release smoke test

## 14. KPI و شاخص‌های سلامت

| KPI | هدف اولیه |
|---|---|
| Build success در main | 100% برای commitهای merge‌شده |
| Calendar round-trip mismatch | 0 |
| Runtime TODO | 0 |
| Consumer compilation failure | 0 در release tag |
| Critical accessibility defect | 0 پیش از stable release |
| Breaking API بدون Major/Deprecation | 0 |
| زمان مهاجرت نمونه از API legacy | کمتر از یک روز کاری برای مصرف‌کننده معمولی |

اعداد مربوط به Build time، startup، binary size و recomposition پس از اجرای CI و benchmark baseline نهایی می‌شوند.

## 15. برنامه انتشار

### مرحله 1 — Internal RC

- Publish به repository داخلی یا GitHub artifact.
- استفاده در Catalog و یک اپ نمونه.
- جمع‌آوری issueهای API و RTL.

### مرحله 2 — Pilot

- استفاده در یک Feature واقعی با Date Picker.
- اجرای migration از API قبلی.
- ثبت مشکلات binary/source compatibility.

### مرحله 3 — Stable 5.6.0

- تمام Release Gateها Passed.
- Changelog و Migration نهایی.
- Tag امضاشده و artifact قابل ردیابی.

### Rollback

- بازگشت مصرف‌کننده به آخرین نسخه پایدار.
- عدم حذف artifact قبلی.
- حفظ API deprecated حداقل تا Major بعدی.
- عدم تغییر migration یا Git history منتشرشده.

## 16. ریسک‌ها و کنترل‌ها

| ریسک | احتمال | اثر | کنترل |
|---|---|---|---|
| عدم Build در Toolchain واقعی | متوسط | بالا | CI روی JDK/SDK رسمی و Fresh resolution |
| خطای Calendar در تاریخ مرزی | کم | بحرانی | full-range round-trip و anchor tests |
| شکستن API مصرف‌کننده | متوسط | بالا | BOM، Maven consumer، API policy و Migration |
| مشکل RTL یا TalkBack | متوسط | بالا | Catalog، UI test و manual accessibility smoke |
| POM ناقص | متوسط | بالا | consumer بدون project dependency |
| Scope creep به App framework | متوسط | متوسط | Non-goals و Module boundary مشخص |
| ادعای زودهنگام Production-Ready | متوسط | بالا | Verification Matrix و release gates اجباری |

## 17. Roadmap بعد از 5.0

- API compatibility snapshot خودکار.
- Baseline Profile و Macrobenchmark پس از اندازه‌گیری.
- Screenshot regression testing.
- Componentهای Sheet، Segmented Button و Search Field در صورت نیاز محصول.
- بررسی امکان KMP فقط برای `calendar-core` پس از تثبیت Android release.
- انتشار Maven Central با signing و provenance.

## 18. تصمیم‌های باز

- Repository نهایی انتشار عمومی یا داخلی.
- SLA پشتیبانی و cadence نسخه‌ها.
- سیاست دقیق deprecation window.
- نیاز واقعی به KMP برای Calendar core.
- Brand palette نهایی و تأیید Design.
- ابزار رسمی screenshot test و API compatibility.

## 19. تعریف Done

نسخه 5.0 زمانی Done است که:

1. تمام نیازمندی‌های Must پیاده‌سازی شده باشند.
2. تمام Gateهای Source، Build، Test، Distribution و Runtime Passed باشند.
3. هیچ Critical/High بدون mitigation و owner باز نباشد.
4. Consumer واقعی با artifact منتشرشده Compile و Run شود.
5. PRD، API، Migration، Changelog، License و Release Notes با artifact منتشر شوند.
6. Rollback به نسخه قبلی مستند و آزمایش شده باشد.

## 20. ماتریس ردیابی نیازمندی‌ها

تمام Requirementهای عملکردی و غیرعملکردی باید به ماژول، Gate و Evidence متصل باشند. منبع machine-readable این ماتریس فایل `release/requirements-traceability.json` است و `scripts/verify-prd-traceability.py` هم‌خوانی آن را با این سند کنترل می‌کند.

### 20.1 خلاصه وضعیت نیازمندی‌ها

| وضعیت | تعداد | تفسیر |
|---|---:|---|
| `build-verification-required` | 8 | پذیرش نهایی به Build یا Lint واقعی Android وابسته است. |
| `configured-not-executed` | 7 | Build/Publishing یا Consumer gate پیکربندی شده ولی اجرا نشده است. |
| `partially-verified` | 10 | بخشی از منطق یا قرارداد اجرا شده، اما Gate Android/Runtime باز است. |
| `standalone-test-passed` | 5 | Verification مستقل غیر Android اجرا و پاس شده است. |
| `statically-reviewed` | 10 | Source و ساختار به‌صورت ایستا بررسی شده‌اند. |
| `test-defined-not-executed` | 15 | تست خودکار وجود دارد، اما در محیط تحویل اجرا نشده است. |

### 20.2 ماتریس Requirement به Gate

| ID | ماژول مسئول | Gate | وضعیت فعلی |
|---|---|---|---|
| FR-001 | `core-ui-foundation` | GATE-B | `build-verification-required` |
| FR-002 | `core-ui-foundation` | GATE-B | `build-verification-required` |
| FR-003 | `core-ui-foundation` | GATE-B | `build-verification-required` |
| FR-004 | `core-ui-foundation` | GATE-B | `build-verification-required` |
| FR-005 | `core-ui-foundation` | GATE-B | `build-verification-required` |
| FR-101 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-102 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-103 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-104 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-105 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-106 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-107 | `core-ui-components` | GATE-C | `test-defined-not-executed` |
| FR-201 | `core-ui-calendar-core` | GATE-C | `standalone-test-passed` |
| FR-202 | `core-ui-calendar-core` | GATE-C | `standalone-test-passed` |
| FR-203 | `core-ui-calendar-core` | GATE-C | `standalone-test-passed` |
| FR-204 | `core-ui-calendar-core` | GATE-C | `standalone-test-passed` |
| FR-205 | `core-ui-calendar-core` | GATE-C | `standalone-test-passed` |
| FR-301 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-302 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-303 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-304 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-305 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-306 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-307 | `core-ui-calendar` | GATE-C | `partially-verified` |
| FR-308 | `core-ui-calendar` | GATE-E | `test-defined-not-executed` |
| FR-309 | `core-ui-calendar` | GATE-E | `test-defined-not-executed` |
| FR-310 | `core-ui-calendar` | GATE-E | `test-defined-not-executed` |
| FR-401 | `distribution` | GATE-D | `configured-not-executed` |
| FR-402 | `distribution` | GATE-D | `configured-not-executed` |
| FR-403 | `distribution` | GATE-D | `configured-not-executed` |
| FR-404 | `distribution` | GATE-D | `configured-not-executed` |
| FR-405 | `distribution` | GATE-D | `configured-not-executed` |
| FR-406 | `documentation` | GATE-A | `statically-reviewed` |
| NFR-001 | `cross-module` | GATE-A | `statically-reviewed` |
| NFR-002 | `cross-module` | GATE-A | `statically-reviewed` |
| NFR-003 | `cross-module` | GATE-A | `statically-reviewed` |
| NFR-004 | `cross-module` | GATE-A | `statically-reviewed` |
| NFR-101 | `foundation/calendar/components` | GATE-C | `partially-verified` |
| NFR-102 | `foundation/calendar/components` | GATE-C | `partially-verified` |
| NFR-103 | `foundation/calendar/components` | GATE-C | `partially-verified` |
| NFR-104 | `release-governance` | GATE-E | `statically-reviewed` |
| NFR-201 | `security` | GATE-A | `statically-reviewed` |
| NFR-202 | `security` | GATE-A | `statically-reviewed` |
| NFR-203 | `security` | GATE-A | `statically-reviewed` |
| NFR-204 | `distribution` | GATE-D | `configured-not-executed` |
| NFR-301 | `toolchain/android` | GATE-B | `build-verification-required` |
| NFR-302 | `toolchain/android` | GATE-B | `build-verification-required` |
| NFR-303 | `toolchain/android` | GATE-B | `build-verification-required` |
| NFR-304 | `core-ui-calendar-core` | GATE-D | `configured-not-executed` |
| NFR-305 | `release-governance` | GATE-A | `statically-reviewed` |
| NFR-401 | `components/calendar/catalog` | GATE-E | `test-defined-not-executed` |
| NFR-402 | `components/calendar/catalog` | GATE-E | `test-defined-not-executed` |
| NFR-403 | `components/calendar/catalog` | GATE-E | `test-defined-not-executed` |
| NFR-404 | `components/calendar/catalog` | GATE-E | `test-defined-not-executed` |
| NFR-405 | `components/calendar/catalog` | GATE-E | `test-defined-not-executed` |

### 20.3 قواعد Traceability

- شناسه Requirement پس از انتشار نباید reuse یا بدون Migration حذف شود.
- هر تغییر Public API باید Requirement، Test/Evidence و Changelog مرتبط داشته باشد.
- وضعیت `Passed` فقط با Evidence اجرایی ثبت می‌شود؛ وجود فایل Test یا Workflow به‌تنهایی کافی نیست.
- PR نمی‌تواند Requirement جدید اضافه کند مگر اینکه Traceability JSON و Acceptance Criteria نیز به‌روزرسانی شوند.

## 21. تصمیم انتشار و Evidence

### 21.1 تصمیم فعلی

**HOLD برای Stable Release؛ مجاز برای Review و اجرای Release Candidate داخلی.**

دلیل Hold، شکست کد تأییدشده نیست؛ Gradle dependency resolution، Android SDK Build، Instrumented Test، Maven publication و Runtime smoke در محیط تحویل قابل اجرا نبوده‌اند.

### 21.2 وضعیت Gateها

| Gate | نام | وضعیت | شرط خروج اصلی |
|---|---|---|---|
| GATE-A | Source and Static | `passed` | Evidence ثبت‌شده |
| GATE-B | Build | `blocked` | Fresh dependency resolution؛ Android debug build؛ Minified release build؛ Release lint |
| GATE-C | Tests | `partial` | Gradle unit test tasks pass؛ Instrumented tests pass on API 26 and current API |
| GATE-D | Distribution | `configured-not-executed` | All publications produced؛ POM inspected؛ Clean Maven consumer compiles؛ Dokka generated؛ checksums recorded |
| GATE-E | Runtime | `not-executed` | Catalog release APK installed؛ Single/Range/Multiple picker smoke؛ RTL/LTR and Persian/English smoke؛ TalkBack smoke؛ Minified release smoke |

### 21.3 Evidence لازم برای تصمیم Go

- لینک CI run مربوط به همان Commit و Tag.
- خروجی Debug و Minified Release Build.
- نتیجه Unit، Lint و Instrumented Tests.
- Maven repository تولیدشده، POMها و نتیجه Consumer Build.
- Checksum artifactها و مقدار `sourceRevision` در Release Manifest.
- گزارش Smoke Test روی API 26 و یک API جاری، در RTL/LTR و Persian/English.

## 22. واژه‌نامه

| واژه | تعریف عملیاتی در این محصول |
|---|---|
| Foundation | ماژول Theme، Token، Typography، Shape، Motion، Sizing، Strings و Adaptive Layout |
| Component | Composable عمومی و reusable که API و رفتار آن بخشی از قرارداد Library است |
| Calendar Core | موتور Pure JVM برای مدل، تبدیل، arithmetic، parsing و formatting تاریخ جلالی |
| Calendar UI | Date Pickerهای Compose با state hoisting، selection policy، RTL و accessibility |
| Aggregate Artifact | Artifact با نام `core-ui` که ماژول‌های عمومی را برای نصب ساده re-export می‌کند |
| BOM | Maven platform برای هم‌تراز نگه‌داشتن version artifactهای انتخابی |
| Consumer Smoke | پروژه‌ای که Public API را از dependency واقعی مصرف و Compile می‌کند |
| Release Gate | مجموعه شرط‌های قابل اثبات که برای انتقال Release به مرحله بعد باید پاس شوند |
| Production-oriented | طراحی و سخت‌سازی با هدف تولید، بدون ادعای عبور از تمام Build/Runtime gateها |
| Production-Ready | وضعیتی که فقط پس از Fresh Build، Test، Runtime، Distribution و Security verification اعلام می‌شود |

## 23. تصویب و Sign-off

| نقش | نام / تیم | تصمیم | تاریخ | Evidence یا توضیح |
|---|---|---|---|---|
| Product Owner | تعیین شود | Pending | — | Scope و KPI |
| Android / Library Owner | ALISCHILLER — MSA | Review required | — | Build و Public API |
| Design System Owner | تعیین شود | Pending | — | Token و Component specification |
| QA / Accessibility | تعیین شود | Pending | — | UI، RTL، TalkBack و adaptive smoke |
| Release Engineer | تعیین شود | Pending | — | Maven، checksum، provenance و rollback |

امضای Stable Release فقط زمانی معتبر است که تمام Gateها `passed` باشند و Release Manifest به Commit و Checksumهای نهایی اشاره کند.

## Author

Developed and maintained by
[ALISCHILLER](https://github.com/ALISCHILLER) — **MSA**

Email: [solimaniali90@gmail.com](mailto:solimaniali90@gmail.com)
