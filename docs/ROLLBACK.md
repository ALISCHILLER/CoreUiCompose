# Rollback Strategy

## General rule

Rollback the smallest cohesive batch. Do not partially revert a public signature while leaving its call sites, tests, Maven metadata or migration guide on the newer contract.

## Toolchain

Revert these together:

- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/libs.versions.toml`
- root/module plugin configuration
- CI JDK/Gradle setup

Then run `./gradlew --version`, `help`, debug/release assembly and consumers before proceeding.

## Public API

- Prefer deprecation over immediate removal.
- Revert aggregate and leaf publication dependencies together.
- If a release was published, publish a new corrective version; do not overwrite an immutable artifact.
- Restore the previous API baseline and migration notes.

## Calendar behavior

Selections are persisted as Gregorian epoch days. Rolling back UI/state code should not rewrite or delete stored dates. Verify old and new versions against the same boundary, disabled-date and range fixtures.

## Publishing

Local artifacts are disposable under `build/repository`. For a remote repository, stop promotion, retain the broken artifact for traceability, publish a corrected version, and direct consumers to the last known-good coordinate.

## Catalog and CI

Catalog changes can be reverted independently unless they expose a compilation failure in public API. CI workflow changes should retain least-privilege permissions and non-persistent checkout credentials even when action versions are rolled back.
