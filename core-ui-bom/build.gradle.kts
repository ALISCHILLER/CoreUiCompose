plugins {
    `java-platform`
    `maven-publish`
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":core-ui-calendar-core"))
        api(project(":core-ui-foundation"))
        api(project(":core-ui-components"))
        api(project(":core-ui-calendar"))
        api(project(":core-ui"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
            artifactId = "core-ui-bom"
            pom {
                name.set("MSA Core UI BOM")
                description.set("Version alignment platform for all MSA Core UI artifacts.")
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
                        name.set(providers.gradleProperty("POM_DEVELOPER_NAME").get())
                        email.set(providers.gradleProperty("POM_DEVELOPER_EMAIL").get())
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
