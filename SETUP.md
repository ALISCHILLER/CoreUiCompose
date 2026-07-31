# Setup

## Required tools

- JDK 17
- Android Studio compatible with AGP 8.13.x
- Android SDK Platform 36
- Android SDK Build Tools installed by Android Studio
- Git

No Node.js, Xcode, CocoaPods or Kotlin Multiplatform toolchain is required. The project is Android-only; `core-ui-calendar-core` is a pure JVM module.

## Local configuration

Create `local.properties` locally when Android Studio does not create it:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

`local.properties` must not be committed.

## First verification

```bash
./gradlew --version
./gradlew help
./scripts/static-audit.sh
./gradlew :core-ui-calendar-core:test
./gradlew :catalog:assembleDebug
```

## Full verification

```bash
./gradlew clean
./gradlew :core-ui-calendar-core:test :core-ui-calendar:testDebugUnitTest
./gradlew :core-ui-foundation:lintRelease :core-ui-components:lintRelease :core-ui-calendar:lintRelease :core-ui:lintRelease
./gradlew :consumer-smoke:assembleRelease :catalog:assembleRelease
./gradlew :dokkaGenerate
./gradlew --stacktrace releaseCandidateCheck
```

Connected tests are exercised in CI on API 26 and API 35; either matching local emulator can run them:

```bash
./gradlew :core-ui-components:connectedDebugAndroidTest :core-ui-calendar:connectedDebugAndroidTest
```

## Generated outputs

```text
build/repository/                 Local Maven repository
build/dokka/html/                 Aggregated API documentation
*/build/outputs/aar/              Android libraries
core-ui-calendar-core/build/libs/ JVM calendar JAR
catalog/build/outputs/apk/        Catalog application
```

## Troubleshooting

### Gradle distribution cannot be downloaded

Confirm DNS/proxy access to `services.gradle.org`. A wrapper download failure does not establish that project compilation is broken.

### Android SDK not found

Open the project once in Android Studio or set `sdk.dir` in the untracked `local.properties` file.

### Maven consumer cannot resolve the artifact

The Maven consumer modules intentionally resolve the generated POM and Gradle module metadata from `build/repository`. Their Android `preBuild` tasks publish all public artifacts first. Because Configuration Cache resolves Kotlin compile classpaths before task execution, only these integration-consumer task graphs opt out of Configuration Cache; normal library and application builds keep using it.

Run either consumer directly from a clean checkout:

```bash
./gradlew --refresh-dependencies :maven-consumer:assembleDebug
./gradlew --refresh-dependencies :maven-calendar-consumer:assembleDebug
```

For an older checkout that does not contain the consumer wiring fix, publish in a separate invocation first:

```bash
./gradlew --no-configuration-cache publishAllPublicationsToBuildRepositoryRepository
./gradlew --refresh-dependencies :maven-calendar-consumer:assembleDebug
```
