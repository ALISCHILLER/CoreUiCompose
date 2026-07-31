import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    `java-library`
    `maven-publish`
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

kotlin {
    explicitApi()
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

dependencies {
    testImplementation(libs.junit4)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "core-ui-calendar-core"
            pom {
                name.set("MSA Core UI Calendar Core")
                description.set("Pure JVM Solar Hijri date model, conversion, parsing, formatting and arithmetic APIs.")
                url.set(providers.gradleProperty("POM_URL").get())
                licenses {
                    license {
                        name.set(providers.gradleProperty("POM_LICENSE_NAME").get())
                        url.set(providers.gradleProperty("POM_LICENSE_URL").get())
                    }
                }
                developers {
                    developer {
                        id.set(providers.gradleProperty("POM_DEVELOPER_ID").get())
                        name.set(providers.gradleProperty("POM_DEVELOPER_NAME").get()); email.set(providers.gradleProperty("POM_DEVELOPER_EMAIL").get())
                    }
                }
                scm {
                    url.set(providers.gradleProperty("POM_SCM_URL").get())
                    connection.set(providers.gradleProperty("POM_SCM_CONNECTION").get())
                    developerConnection.set(providers.gradleProperty("POM_SCM_DEV_CONNECTION").get())
                }
            }
        }
    }
    repositories {
        maven {
            name = "buildRepository"
            url = rootProject.layout.buildDirectory.dir("repository").get().asFile.toURI()
        }
    }
}
