rootProject.name = "remocra"

pluginManagement {
    repositories {
        // gradlePluginPortal redirige vers JCenter qui n'est pas fiable,
        // on préfère Central à la place de JCenter (pour les mêmes dépendances)
        // cf. https://github.com/gradle/gradle/issues/15406
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        maven(url = "https://repo.osgeo.org/repository/release/") {
            content {
                includeGroup("it.geosolutions.jgridshift")
                includeGroup("org.geotools")
                includeGroup("org.geotools.ogc")
                includeGroup("javax.media")
            }
        }
        mavenCentral()
    }
}
include("db", "app")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
