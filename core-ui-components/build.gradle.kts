import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

android {
    namespace = "com.msa.coreui.components"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes { release { isMinifyEnabled = false } }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }
    publishing { singleVariant("release") { withSourcesJar() } }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        warningsAsErrors = true
    }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

composeCompiler {
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    api(project(":core-ui-foundation"))
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
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
                artifactId = "core-ui-components"
                pom {
                    name.set("MSA Core UI Components")
                    description.set("Accessible Material 3 screens, navigation, tabs, sheets, avatars, skeletons, buttons, fields, feedback and state components.")
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
