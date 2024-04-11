pluginManagement {
    repositories {
        // gradlePluginPortal redirige vers JCenter qui n'est pas fiable,
        // on préfère Central à la place de JCenter (pour les mêmes dépendances)
        // cf. https://github.com/gradle/gradle/issues/15406
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        // gradlePluginPortal redirige vers JCenter qui n'est pas fiable,
        // on préfère Central à la place de JCenter (pour les mêmes dépendances)
        // cf. https://github.com/gradle/gradle/issues/15406
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
