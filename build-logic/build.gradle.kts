plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(plugin(libs.plugins.kotlin))
    implementation(plugin(libs.plugins.android.lint))
    implementation(plugin(libs.plugins.spotless))
    implementation(plugin(libs.plugins.loggingCapabilities))
    implementation(plugin(libs.plugins.forbiddenapis))
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "src/main/kotlin/**/*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}

// Pour simplifier la déclaration de dépendances sur des plugins Gradle
// https://github.com/gradle/gradle/issues/17963
// https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers
fun plugin(plugin: Provider<PluginDependency>) = plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
