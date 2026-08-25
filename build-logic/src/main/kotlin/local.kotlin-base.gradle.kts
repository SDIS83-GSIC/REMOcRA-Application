import com.android.build.gradle.internal.packaging.defaultExcludes

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
        languageVersion.set(JavaLanguageVersion.of(versionCatalogs.named("libs").findVersion("javaToolchain").orElseThrow().requiredVersion))
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
        ktlint(versionCatalogs.named("libs").findVersion("ktlint").orElseThrow().requiredVersion)
    }
}
// Évite une race condition entre Spotless (qui crée/supprime des fichiers
// temporaires dans build/spotless/spotlessKotlin/) et Android Lint qui scanne
// les sources Kotlin en parallèle : cf.
// > A failure occurred while executing com.android.build.gradle.internal.lint.AndroidLintWorkAction
//   > java.io.FileNotFoundException: /var/lib/jenkins/jenkins-agent-linux-02-atolsi-prod/workspace/team--kfc/atolcd--remocra
//   /atolcd--remocra-v3--gerrit-trigger/db/build/spotless/spotlessKotlin/src/main/jooq/remocra/db/jooq/
//   couverturehydraulique/Couverturehydraulique.kt (No such file or directory)
tasks.matching { it.name.startsWith("lintAnalyze") || it.name.startsWith("lintReport") || it.name == "lint" }
    .configureEach {
        mustRunAfter(tasks.matching { it.name.startsWith("spotless") })
    }
