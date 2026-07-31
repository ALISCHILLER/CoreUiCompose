# Dependency Upgrade Report

Review date: 2026-07-30

## Decision summary

The project intentionally remains on the latest compatible 8.x Android toolchain batch already configured. A current AGP 9.x migration is not mixed into this correctness patch because it requires Gradle 9.x, broader DSL/build behavior review, API 37 planning and a successful baseline Android build for comparison and rollback.

## Current and selected stack

| Component | Project | Current ecosystem observation | Decision |
|---|---:|---:|---|
| JDK | 17 | AGP 8.13 and current AGP 9.x use JDK 17 | Retain |
| Gradle Wrapper | 8.13 | Required/default for AGP 8.13; AGP 9.3 requires Gradle 9.5 | Retain in current branch |
| Android Gradle Plugin | 8.13.2 | 9.3.1 is current stable at review time | Defer major migration to isolated branch |
| Kotlin | 2.3.21 | Newer 2.4.x releases exist, but AGP 8.13.2 explicitly supports Kotlin 2.3 | Retain compatible line |
| Compose BOM | 2026.06.00 | Current stable BOM documentation still uses 2026.06.00 | Retain |
| Activity Compose | 1.13.0 | Current stable | Retain |
| Dokka | 2.2.0 | DGP v2 configuration is supported with this Gradle/AGP/Kotlin baseline | Retain pending Gradle execution |

## Official compatibility basis

- AGP current release/API reference: <https://developer.android.com/reference/tools/gradle-api>
- AGP 8.13 release notes and compatibility: <https://developer.android.com/build/releases/agp-8-13-0-release-notes>
- AGP 9.3 release notes and compatibility: <https://developer.android.com/build/releases/agp-9-3-0-release-notes>
- Compose BOM: <https://developer.android.com/develop/ui/compose/bom>
- Compose compiler/setup requirements: <https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler>
- Activity releases: <https://developer.android.com/jetpack/androidx/releases/activity>
- Kotlin releases: <https://github.com/JetBrains/kotlin/releases>
- Dokka Gradle plugin: <https://kotlinlang.org/docs/dokka-gradle.html>

## Why the AGP 9.x / Kotlin 2.4 migration is deferred

1. No clean Gradle/Android baseline could execute in the delivery environment.
2. AGP 9.3 moves the Gradle baseline to 9.5 and supports API 37; this is a toolchain migration, not an isolated patch.
3. Newer Compose lines can require compileSdk 37 and AGP 9, coupling UI and build-system upgrades.
4. Mixing that migration with a calendar correctness/security patch would weaken reviewability and rollback.
5. The current AGP 8.13.2 / Gradle 8.13 / JDK 17 / Kotlin 2.3.21 combination is internally aligned for compileSdk 36.

## Required migration branch

1. Run `releaseCandidateCheck` and record build, lint, test, POM, R8 and runtime baselines.
2. Upgrade Gradle and AGP as one batch using the official matrix.
3. Resolve AGP 9 DSL/API deprecations and configuration-cache issues without unrelated UI changes.
4. Upgrade compileSdk/target planning and Kotlin/Compose in separate reviewable commits.
5. Re-run debug/release, unit/instrumented tests, Dokka, all publications and both consumer projects.
6. Compare generated POMs, AAR/JAR contents, public API and minified runtime behavior.
7. Keep the current 8.13 branch available until the migration passes all release gates.

## Remaining verification

Dependency resolution, Android debug/release builds, lint, Dokka, Compose UI tests, Maven publication and runtime smoke must run in a networked Android environment before release.
