import gradle.kotlin.dsl.accessors._1cd57f1730507bf1765dcbcf508dc5fb.spotless
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("local.base")
    id("local.dependency-management")
    id("de.thetaphi.forbiddenapis")
    kotlin("jvm")
    id("com.android.lint")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(project.the<VersionCatalogsExtension>().named("libs").findVersion("javaToolchain").orElseThrow().requiredVersion))
    }
}

// On veut utiliser la toolchain pour toutes les tâches JavaExec,
// ce qui n'est pas le cas par défaut
// https://github.com/gradle/gradle/issues/16791
tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

tasks.withType<Jar>().configureEach {
    // N'inclut jamais la version dans les noms des JARs
    archiveVersion.set(null as String?)
}

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(versionCatalogs.named("libs").findVersion("junit").orElseThrow().requiredVersion)
        }
    }
}

forbiddenApis {
    bundledSignatures = setOf("jdk-unsafe", "jdk-deprecated", "jdk-internal", "jdk-non-portable", "jdk-system-out")
}

lint {
    abortOnError = true
    warningsAsErrors = true
    disable += setOf("TrulyRandom", "GradleDependency")
}

spotless {
    kotlin {
        ktlint(project.the<VersionCatalogsExtension>().named("libs").findVersion("ktlint").orElseThrow().requiredVersion)
    }
}
