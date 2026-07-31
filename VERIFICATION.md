# Verification

Verification date: 2026-07-31

## Environment

```text
Host JDK: 21 available
Project toolchain: JDK/JVM 17
Local Kotlin compiler: 1.9.0
Android SDK: unavailable
Gradle 8.13 distribution: unavailable locally
Network access to services.gradle.org: unavailable
```

## Executive result

The source distribution is statically hardened and the calendar core/state contracts are reproducibly JVM-verified. Android dependency resolution, Android builds, lint, Dokka, Maven publication, emulator tests and runtime smoke remain blocked or not executed in this delivery environment.

No result below is inferred from the presence of a test or workflow alone.

## Critical corrections verified in this pass

| Finding | Severity | Correction | Verification |
|---|---|---|---|
| Catalog Navigation and Layout cards routed back to Home and no detail screens existed | High UX | Added real detail destinations, RTL-aware back behavior, interactive showcases and ordinal-independent primary navigation mapping | Static route contract passes; Compose UI regressions added but not executed in this environment |
| Extreme Gregorian years could overflow the engine's `Int` day-number calculation and alias to an apparently valid Persian date | High | Added explicit Gregorian boundaries before conversion and public boundary constants | Regression covers `LocalDate.MIN`, `LocalDate.MAX` and a previously aliased year |
| Large month-navigation deltas could clamp onto a disabled boundary month and incorrectly refuse movement even when a selectable month existed in the requested direction | Medium | Added a bounded opposite-search fallback that cannot cross the current month | Forward and backward overshoot tests pass |
| `allowEmptySelectionConfirmation=true` was overridden when every date was disabled | Medium | Empty-confirmation policy now takes precedence for an empty selection | No-selectable-date empty-confirmation test passes |
| Fully disabled full-range configurations could scan the entire supported calendar during initialization/navigation checks | Medium | Added exact short-circuits for all weekdays disabled and complete explicit boundary coverage | Full-boundary state regression passes; local probe initialization improved from about 147 ms to about 19 ms |
| Documentation claimed checks that were not all implemented by the shipped audit script | Medium | Expanded `static-audit.sh` and added a reproducible standalone calendar verifier | Both scripts execute successfully from the delivered tree |

## Executed checks

| Check | Result | Evidence |
|---|---|---|
| Repository static audit | Passed | `scripts/static-audit.sh` |
| Catalog Navigation/Layout destination contract | Passed statically | Cards route to real pages; `selectedIndex` no longer uses `CatalogPage.ordinal`; page tags and UI tests present |
| Distribution structure gate | Passed | `scripts/verify-distribution.sh` |
| PRD traceability | Passed | 55 requirements, 5 gates |
| Version alignment | Passed | Library `5.7.0-SNAPSHOT`, Catalog `5.7.0`, versionCode `50700` |
| Merge-conflict and runtime-placeholder scans | Passed | Production source/config scan |
| Compose compatibility regression scan | Passed | Scope-only imports, obsolete path API, pointer import and `Dp.Zero` guarded |
| Material/chronology/fixed-offset scan | Passed | No Material 2 import, Hijrah chronology or fixed Iran offset |
| Dynamic/insecure dependency configuration scan | Passed | No dynamic versions or insecure Gradle repository transport |
| Secret/signing-material scan | Passed | Sensitive filenames and high-confidence credential patterns |
| Shell/Python syntax | Passed | All scripts parsed/compiled |
| XML/TOML/JSON parsing | Passed | 14 XML, version catalog, 3 release JSON files |
| Workflow/Dependabot YAML parsing | Passed | 5 workflows plus Dependabot |
| Workflow security baseline | Passed | Top-level permissions, no `pull_request_target`, no floating branch refs, checkout credentials disabled |
| Catalog manifest security/localization contract | Passed | Backup disabled, cleartext disabled, RTL/locales configured, exported component explicit |
| Bilingual Catalog resource-key parity | Passed | English and Persian resource keys match |
| Gradle module shape | Passed | 10 configured modules |
| Gradle wrapper JAR integrity | Passed | ZIP integrity check |
| Wrapper distribution checksum configured | Passed statically | One 64-character SHA-256 value |
| Calendar-core strict explicit API compile | Passed | `kotlinc -Xexplicit-api=strict -Werror` |
| Calendar-core JVM regression suite | Passed | 21 tests |
| Full supported Persian range round trip | Passed | 1,182,655 valid Persian dates |
| Extreme Gregorian overflow regression | Passed | Invalid extreme years no longer alias into the supported Persian range |
| Date-picker state/localization/contract suite | Passed | 49 tests using minimal compile-time Compose stubs |
| Total standalone calendar checks | Passed | 70 tests, zero failures |
| Gradle wrapper launch | Blocked | `UnknownHostException: services.gradle.org` |

The standalone test harness is shipped as `scripts/verify-calendar-standalone.sh`. It verifies pure JVM logic and selected non-rendering Compose state/contracts; it does not replace Android Gradle, lint, emulator or runtime verification.

## Verification matrix

```text
Source Review: Statically Reviewed
Gradle Configuration: Statically Reviewed
Dependency Resolution: Blocked - Gradle distribution/dependencies unavailable
Android Debug Build: Not Executed
Android Release Build: Not Executed
Unit Tests: Partially Verified - 70 standalone calendar/core/state tests passed; Gradle test tasks not executed
Compose UI Tests: Not Executed - test sources/workflows exist, emulator execution not performed
Instrumented Tests: Not Executed
KMP Common Tests: Not Applicable - project is Android-only; calendar-core is JVM, not KMP
Android Runtime: Not Executed
Desktop Build: Not Applicable
JS/Wasm Build: Not Applicable
iOS Compilation: Not Applicable
Database Migration: Not Applicable
Network Integration: Not Applicable
Localization: Partially Verified - string parity, formatter and immutable localization contracts passed; runtime locale smoke not executed
Persian RTL / English LTR: Statically reviewed; runtime smoke not executed
Language Persistence: Test sources present; Gradle/runtime verification not executed in this pass
Application Showcase Image: Present and linked; it remains identified as a design-aligned mockup
Security Review: Static baseline passed; CodeQL/dependency-review workflow runs not verified
CI/CD: Configured and YAML/security-baseline validated; actual workflow runs not verified
Publishing: Configured; POM/artifact publication not executed
Consumer Projects: Configured; Gradle/Maven consumer builds not executed
License Compliance: Attribution retained and statically reviewed; not legal advice
Production Readiness: Production-oriented candidate; not production-ready until external build/runtime gates pass
```

## Blocked Gradle evidence

The wrapper attempted to download:

```text
https://services.gradle.org/distributions/gradle-8.13-bin.zip
```

and failed with:

```text
java.net.UnknownHostException: services.gradle.org
```

The Android SDK is also unavailable. This is an environment block, not evidence that the project build passed or failed.

## Reproduce available checks

```bash
./scripts/static-audit.sh
./scripts/verify-distribution.sh
./scripts/verify-calendar-standalone.sh
```

## Required external release run

Run in a clean, networked environment with Android SDK 36 and JDK 17:

```bash
./gradlew --version
./gradlew clean
./gradlew --stacktrace releaseCandidateCheck
./gradlew \
  :core-ui-components:connectedDebugAndroidTest \
  :core-ui-calendar:connectedDebugAndroidTest \
  :catalog:connectedDebugAndroidTest
```

A stable release also requires manual Catalog smoke testing in light/dark, Persian/English, RTL/LTR, large font, minimum API and a current API, plus inspection of generated POMs, R8 output, artifacts and checksums.
