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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
include("db", "app")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
