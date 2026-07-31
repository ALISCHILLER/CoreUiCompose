import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

android {
    namespace = "com.msa.coreui"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "CORE_UI_VERSION", "\"${project.version}\"")
    }

    buildTypes { release { isMinifyEnabled = false } }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { buildConfig = true }
    publishing { singleVariant("release") { withSourcesJar() } }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        warningsAsErrors = true
    }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies {
    api(project(":core-ui-calendar-core"))
    api(project(":core-ui-foundation"))
    api(project(":core-ui-components"))
    api(project(":core-ui-calendar"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                versionMapping {
                    usage("java-api") { fromResolutionOf("releaseRuntimeClasspath") }
                    usage("java-runtime") { fromResolutionOf("releaseRuntimeClasspath") }
                }
                artifactId = "core-ui"
                pom {
                    name.set(providers.gradleProperty("POM_NAME").get())
                    description.set(providers.gradleProperty("POM_DESCRIPTION").get())
                    url.set(providers.gradleProperty("POM_URL").get())
                    licenses { license { name.set(providers.gradleProperty("POM_LICENSE_NAME").get()); url.set(providers.gradleProperty("POM_LICENSE_URL").get()) } }
                    developers { developer { id.set(providers.gradleProperty("POM_DEVELOPER_ID").get()); name.set(providers.gradleProperty("POM_DEVELOPER_NAME").get()); email.set(providers.gradleProperty("POM_DEVELOPER_EMAIL").get()) } }
                    scm { url.set(providers.gradleProperty("POM_SCM_URL").get()); connection.set(providers.gradleProperty("POM_SCM_CONNECTION").get()); developerConnection.set(providers.gradleProperty("POM_SCM_DEV_CONNECTION").get()) }
                }
            }
        }
        repositories { maven { name = "buildRepository"; url = rootProject.layout.buildDirectory.dir("repository").get().asFile.toURI() } }
    }
}
