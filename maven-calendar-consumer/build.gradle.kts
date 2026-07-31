import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.msa.coreui.mavencalendarconsumer"
    compileSdk = 36

    defaultConfig { minSdk = 26 }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }
    lint {
        abortOnError = true
        warningsAsErrors = false
    }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

val coreUiGroup = providers.gradleProperty("GROUP").get()
val coreUiVersion = providers.gradleProperty("VERSION_NAME").get()

composeCompiler {
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    // Intentionally depend on the calendar artifact only. Its public field API exposes
    // CoreFieldStatus, so the published POM must carry core-ui-components transitively.
    implementation(platform("$coreUiGroup:core-ui-bom:$coreUiVersion"))
    implementation("$coreUiGroup:core-ui-calendar")
}
