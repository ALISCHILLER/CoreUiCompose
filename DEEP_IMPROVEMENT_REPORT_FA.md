# گزارش بهبود عمیق MSA Core UI 5.7.0

تاریخ بازبینی: ۳۱ ژوئیه ۲۰۲۶

## جمع‌بندی اجرایی

پروژه به‌صورت Android-only و چندماژوله بررسی شد؛ `core-ui-calendar-core` یک کتابخانه خالص JVM است و سایر بخش‌ها شامل Foundation، Components، Calendar Compose، BOM، Catalog و Consumerهای پروژه/Maven هستند.

خروجی این مرحله فقط بازآرایی ظاهری نیست. یک خطای واقعی و پرریسک در تبدیل تاریخ، دو نقص رفتاری در State و Navigation، یک مسیر پرهزینه در بازه‌های کاملاً غیرفعال و یک شکاف مهم میان ادعاهای مستندات و ابزارهای Verification اصلاح شده‌اند.

وضعیت نهایی این تحویل:

```text
Source Review: Statically Reviewed
Static/Distribution Gates: Passed
Standalone Calendar Tests: 70 Passed, 0 Failed
Android/Gradle Build: Blocked by environment
Android Runtime: Not Executed
Production Readiness: Candidate; not production-ready
```

## یافته‌های اصلی و اصلاحات

### ۰. مقصدهای Navigation و Layout در Catalog وجود نداشتند — High UX

**مشکل:** کارت‌های «ناوبری» و «چیدمان» هر دو به `CatalogPage.HOME` متصل بودند؛ لمس آن‌ها فقط صفحه خانه را دوباره انتخاب می‌کرد و هیچ صفحه‌ای نمایش داده نمی‌شد. همچنین `page.ordinal` مستقیماً به `selectedIndex` نوار اصلی داده می‌شد که با اضافه‌شدن صفحه‌های جزئی می‌توانست index نامعتبر بسازد.

**اصلاح:** دو مقصد واقعی `CatalogPage.NAVIGATION` و `CatalogPage.LAYOUT` اضافه شد. صفحه Navigation شامل Bottom bar، Navigation rail، badge و state کنترل‌شده است. صفحه Layout شامل Adaptive grid، Responsive form row و Master-detail برای compact/medium/expanded است. صفحه‌های جزئی دکمه بازگشت RTL-aware دارند و انتخاب نوار اصلی از enum ordinal جدا شده است.

**Verification:** کنترل رگرسیون `static-audit.sh` مسیر کارت‌ها، وجود صفحه‌ها، tagها و نبود اتصال ordinal را بررسی می‌کند. دو Compose UI test نیز اضافه شده‌اند؛ اجرای runtime آن‌ها در این محیط به‌دلیل نبود Android SDK و Gradle distribution انجام نشد.

### ۱. سرریز عددی در تبدیل Gregorian به Persian — High

**مشکل:** موتور تبدیل پیش از بررسی محدوده، سال میلادی را وارد محاسبات `Int` می‌کرد. برای بعضی سال‌های بسیار دور، overflow باعث می‌شد تاریخ نامعتبر به یک تاریخ ظاهراً معتبر شمسی نگاشت شود.

**اصلاح:** محدوده دقیق میلادی پیش از هر محاسبه بررسی می‌شود:

```text
Minimum: 0560-03-20
Maximum: 3798-03-19
```

دو مقدار عمومی نیز اضافه شد:

```kotlin
PersianCalendar.MIN_SUPPORTED_GREGORIAN_DATE
PersianCalendar.MAX_SUPPORTED_GREGORIAN_DATE
```

**Verification:** تاریخ‌های `LocalDate.MIN`، `LocalDate.MAX` و یک نمونه‌ای که قبلاً به‌اشتباه معتبر می‌شد، اکنون به‌صورت قطعی رد می‌شوند. کل ۱٬۱۸۲٬۶۵۵ تاریخ معتبر شمسی همچنان round-trip شده است.

### ۲. خطای Navigation در deltaهای بزرگ — Medium

**مشکل:** وقتی delta بزرگ به اولین/آخرین ماه مجاز clamp می‌شد و آن ماه کاملاً غیرفعال بود، Navigation ممکن بود با وجود ماه قابل انتخاب در همان جهت، `false` برگرداند.

**اصلاح:** جست‌وجوی fallback محدود اضافه شد؛ نزدیک‌ترین ماه قابل انتخاب را پیدا می‌کند، اما از ماه فعلی عبور نمی‌کند و جهت درخواست را معکوس نمی‌کند.

### ۳. ناسازگاری `allowEmptySelectionConfirmation` — Medium

**مشکل:** حتی وقتی تأیید انتخاب خالی صریحاً مجاز بود، در حالتی که هیچ تاریخ قابل انتخابی وجود نداشت دکمه تأیید غیرفعال می‌شد.

**اصلاح:** سیاست صریح انتخاب خالی برای Selection خالی اولویت دارد و اکنون رفتار آن در تمام حالت‌ها یکسان است.

### ۴. اسکن غیرضروری کل محدوده غیرفعال — Medium

**مشکل:** برای تنظیماتی مانند غیرفعال بودن هر هفت روز هفته یا پوشش صریح کل بازه با disabled range، State می‌توانست ماه‌ها/روزهای بسیار زیادی را اسکن کند.

**اصلاح:** تشخیص قطعی «کل بازه غیرفعال» اضافه شد و مسیرهای `hasSelectableDates`، Navigation و nearest-date کوتاه می‌شوند.

**پروب محلی:** مقدار initialization در بازه کامل پشتیبانی‌شده از حدود ۱۴۷ ms به حدود ۱۹ ms کاهش یافت. این عدد فقط پروب JVM همین محیط است و جایگزین Android Benchmark نیست.

### ۵. شکاف Evidence در Verification — Medium

**مشکل:** `VERIFICATION.md` تعدادی کنترل را انجام‌شده معرفی می‌کرد، اما نسخه قبلی `static-audit.sh` همه آن‌ها را اجرا نمی‌کرد و standalone verifier قابل‌تکرار نیز داخل بسته وجود نداشت.

**اصلاحات ابزار:** اکنون ممیزی شامل موارد زیر است:

- syntax تمام Shell و Python scriptها
- parsing فایل‌های XML، TOML، JSON و YAML
- trailing whitespace
- integrity فایل Gradle Wrapper JAR و وجود SHA-256 توزیع
- تطابق نسخه Library/Catalog/versionCode
- ساختار ۱۰ ماژول Gradle
- parity کلیدهای فارسی/انگلیسی Catalog
- تنظیمات امنیتی Manifest
- baseline امنیت Workflowها و غیرفعال بودن checkout credentials
- Secret/signing-material و dependency transport scan
- regression scan برای Material 2، Hijrah، offset ثابت و APIهای ناسازگار Compose

اسکریپت جدید `scripts/verify-calendar-standalone.sh` نیز ۲۱ تست core و ۴۹ تست State/Formatting/Contract را اجرا می‌کند.

## تصمیم Toolchain و Dependency

نسخه‌های فعلی پروژه از نظر داخلی هم‌راستا هستند:

```text
JDK 17
Gradle 8.13
AGP 8.13.2
Kotlin 2.3.21
Compose BOM 2026.06.00
Activity Compose 1.13.0
compileSdk/targetSdk 36
```

شاخه پایدار جدید AGP در زمان بازبینی 9.x است، اما انتقال به آن به Gradle 9.x، بررسی DSL/API، برنامه compileSdk 37، Build baseline و تست R8/Publishing نیاز دارد. این مهاجرت عمداً با Patch اصلاح صحت تقویم مخلوط نشده و مسیر آن در `DEPENDENCY_UPGRADE_REPORT.md` ثبت شده است.

## Verification اجراشده

```bash
./scripts/static-audit.sh
./scripts/verify-distribution.sh
./scripts/verify-calendar-standalone.sh
```

نتیجه:

- Static audit: Passed
- Distribution verification: Passed
- Strict explicit API compile: Passed
- Calendar core: 21 Passed
- Calendar State/Formatting/Contract: 49 Passed
- Total: 70 Passed, 0 Failed
- Full Persian range round-trip: 1,182,655 dates

## مواردی که اجرا نشدند

به‌دلیل نبود Android SDK و عدم دسترسی شبکه به `services.gradle.org`، موارد زیر اجرا نشدند:

- Gradle dependency resolution
- Android debug/release build
- lintRelease
- R8/minified runtime smoke
- Dokka
- Maven publication و consumer build
- Instrumented/Compose UI tests
- RTL/LTR، TalkBack، large-font و runtime locale smoke

خطای Gradle Wrapper:

```text
java.net.UnknownHostException: services.gradle.org
```

این وضعیت «Blocked» است، نه Passed و نه اثبات خرابی پروژه.

## فایل‌های مهم تغییرکرده

- `core-ui-calendar-core/.../PersianCalendarEngine.kt`
- `core-ui-calendar-core/.../PersianCalendar.kt`
- `core-ui-calendar/.../PersianDatePickerState.kt`
- تست‌های calendar-core و date-picker state
- `scripts/static-audit.sh`
- `scripts/verify-calendar-standalone.sh`
- `VERIFICATION.md`
- `DEEP_TRANSFORMATION_REPORT.md`
- `DEPENDENCY_UPGRADE_REPORT.md`
- `README.md`, `API.md`, `MIGRATION.md`, `CHANGELOG.md`
- release gates/manifest/traceability

## Gate نهایی انتشار

برای Release واقعی، در محیط تمیز و networked با JDK 17 و Android SDK 36 اجرا شود:

```bash
./gradlew clean --stacktrace releaseCandidateCheck
./gradlew \
  :core-ui-components:connectedDebugAndroidTest \
  :core-ui-calendar:connectedDebugAndroidTest \
  :catalog:connectedDebugAndroidTest
```

سپس POM، AAR/JAR، checksum، R8 mapping، Catalog release APK و smokeهای دسترسی‌پذیری/RTL/LTR بررسی شوند.
