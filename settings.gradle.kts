pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        exclusiveContent {
            forRepository {
                maven {
                    name = "buildRepository"
                    url = uri(rootDir.resolve("build/repository"))
                }
            }
            filter { includeGroup("com.msa.coreui") }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "MSA-Core-UI"

include(":core-ui-foundation")
include(":core-ui-components")
include(":core-ui-calendar-core")
include(":core-ui-calendar")
include(":core-ui")
include(":core-ui-bom")
include(":catalog")
include(":consumer-smoke")
include(":maven-consumer")
include(":maven-calendar-consumer")
