import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.dokka)
}

allprojects {
    group = providers.gradleProperty("GROUP").get()
    version = providers.gradleProperty("VERSION_NAME").get()

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<Test>().configureEach {
        failFast = false
        testLogging {
            events("failed", "skipped")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = false
        }
    }
}

dependencies {
    dokka(project(":core-ui-foundation"))
    dokka(project(":core-ui-components"))
    dokka(project(":core-ui-calendar-core"))
    dokka(project(":core-ui-calendar"))
    dokka(project(":core-ui"))
}

dokka {
    moduleName.set("MSA Core UI")
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
        includes.from(layout.projectDirectory.file("README.md"))
    }
}

val staticAudit by tasks.registering(Exec::class) {
    group = "verification"
    description = "Runs source, dependency, secret, XML, and distribution static checks."
    commandLine("bash", "scripts/static-audit.sh")
}

val verifyVersionAlignment by tasks.registering(Exec::class) {
    group = "verification"
    description = "Verifies that library, catalog, and release versions are aligned."
    commandLine("bash", "scripts/check-version-alignment.sh")
}

val verifySourceDistribution by tasks.registering(Exec::class) {
    group = "verification"
    description = "Validates required source-distribution files and release metadata."
    commandLine("bash", "scripts/verify-distribution.sh")
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs the fast pull-request quality gate for public modules and consumers."
    dependsOn(
        staticAudit,
        verifyVersionAlignment,
        ":core-ui-calendar-core:test",
        ":core-ui-foundation:testDebugUnitTest",
        ":core-ui-components:testDebugUnitTest",
        ":core-ui-calendar:testDebugUnitTest",
        ":catalog:testDebugUnitTest",
        ":core-ui-foundation:lintRelease",
        ":core-ui-components:lintRelease",
        ":core-ui-calendar:lintRelease",
        ":core-ui:lintRelease",
        ":consumer-smoke:assembleDebug",
        ":catalog:assembleDebug",
    )
}

val publishAllPublicationsToBuildRepositoryRepository by tasks.registering {
    group = "publishing"
    description = "Publishes every public Core UI artifact to the local build repository."
    dependsOn(
        ":core-ui-bom:publishMavenPublicationToBuildRepositoryRepository",
        ":core-ui-calendar-core:publishMavenPublicationToBuildRepositoryRepository",
        ":core-ui-foundation:publishReleasePublicationToBuildRepositoryRepository",
        ":core-ui-components:publishReleasePublicationToBuildRepositoryRepository",
        ":core-ui-calendar:publishReleasePublicationToBuildRepositoryRepository",
        ":core-ui:publishReleasePublicationToBuildRepositoryRepository",
    )
}

tasks.register("releaseCandidateCheck") {
    group = "verification"
    description = "Builds minified consumers, documentation, publications, and Maven metadata."
    dependsOn(
        "qualityCheck",
        ":consumer-smoke:assembleRelease",
        ":catalog:assembleRelease",
        ":dokkaGenerate",
        publishAllPublicationsToBuildRepositoryRepository,
        verifySourceDistribution,
        ":maven-consumer:assembleRelease",
        ":maven-calendar-consumer:assembleRelease",
    )
}



tasks.register<Zip>("sourceDistribution") {
    group = "distribution"
    description = "Creates a reproducible source archive without local caches, build output, or secrets."
    dependsOn(verifySourceDistribution)
    archiveBaseName.set("MSA-Core-UI")
    archiveVersion.set(project.version.toString())
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    from(rootDir) {
        exclude(
            ".git/**",
            ".gradle/**",
            ".idea/**",
            "**/build/**",
            "local.properties",
            "**/*.iml",
            "**/*.jks",
            "**/*.keystore",
            "**/*.p12",
            "**/*.pem",
        )
    }
}

// Maven consumers intentionally resolve artifacts from build/repository instead of
// using project dependencies, so they validate the published POM and Gradle metadata.
// Configuration Cache resolves Kotlin compile classpaths before task execution; on a
// clean checkout that happens before the repository-producing publish tasks can run.
// Disable Configuration Cache only for consumer build graphs and preserve it for all
// normal library, Catalog, test, lint, and documentation tasks.
listOf(":maven-consumer", ":maven-calendar-consumer").forEach { consumerPath ->
    project(consumerPath).tasks.matching { it.name == "preBuild" }.configureEach {
        dependsOn(publishAllPublicationsToBuildRepositoryRepository)
        notCompatibleWithConfigurationCache(
            "Consumes Maven artifacts published to build/repository earlier in the same invocation.",
        )
    }
}
