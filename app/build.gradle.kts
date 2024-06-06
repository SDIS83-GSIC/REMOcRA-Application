import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("local.kotlin-base")
    kotlin("jvm")
    idea
    alias(libs.plugins.cyclonedx)
}

dependencies {
    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.log4j.jul)
    implementation(libs.log4j.core)
    implementation(platform(libs.log4j.bom))
    runtimeOnly(libs.slf4j.api)
    runtimeOnly(libs.log4j.slf4jImpl)
    runtimeOnly(libs.log4j.bom)
    runtimeOnly(libs.log4j.core)
    implementation(libs.disruptor)

    // Utilitaires
    compileOnly(libs.forbiddenapis)
    implementation(libs.typesafe)
    implementation(libs.picocli)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.commons.email)

    // Dependency Injection
    implementation(libs.guice.bom)
    implementation(libs.kotlin.guice)

    // Base de données
    implementation(projects.db)
    implementation(libs.hikaricp)
    implementation(libs.jooq.kotlin)
    runtimeOnly(libs.postgresql)
    implementation(libs.flyway.core)

    // Web
    implementation(libs.jetty.servlet)
    implementation(libs.jetty.servlets)
    implementation(platform(libs.jetty.bom))

    implementation(libs.resteasy.core.spi)
    implementation(libs.resteasy.core)
    implementation(libs.resteasy.multipart.provider)
    implementation(platform(libs.resteasy.bom))

    // JSON
    implementation(libs.jackson.jakarta.rs.json.provider)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.datatype.guava)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jackson.dataformat.csv)

    // Pac4j (authn)
    implementation(libs.jakartaee.pac4j)
    implementation(libs.pac4j.oidc)
    implementation(libs.pac4j.jakartaee)

    // Tests
    testCompileOnly(libs.forbiddenapis)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

object Props {
    private val map = mutableMapOf<String, String>()

    var name by map
    var description by map
    var user by map
    var group by map
    var mainclass by map

    init {
        name = "remocra"
        description = "remocra"
        user = "remocra"
        group = "remocra"
        mainclass = "remocra.cli.Main"
    }

    fun asMap(): Map<String, String> = map
}

var frontendOutputDir = "$rootDir/frontend/build/parceljs"

tasks {
    processResources {
        val versionProperties = mapOf("appVersion" to (project.findProperty("ospackageVersion")?.toString() ?: "dev"))
        inputs.property("versionProperties", versionProperties)
        filesMatching("reference.conf") {
            filter<ReplaceTokens>("tokens" to versionProperties)
        }
    }
    test {
        useJUnitPlatform {
            excludeTags("postgres")
        }
    }
    register<Test>("pgTest") {
        description = "Lance les tests nécessitant Postgresql"
        group = LifecycleBasePlugin.VERIFICATION_GROUP

        systemProperties = project.properties.filterKeys { it.startsWith("remocra.") }
        useJUnitPlatform {
            includeTags("postgres")
        }
    }
    fun JavaExec.cli(vararg extraArgs: String) {
        group = "cli"
        description = "Lance l'application"
        classpath = sourceSets["main"].runtimeClasspath
        mainClass = Props.mainclass
        args(*extraArgs)
        systemProperties = mapOf(
            "remocra.http.static-dir" to frontendOutputDir,
            "remocra.http.session-store-dir" to "${project.layout.buildDirectory.asFile.get()}/session",
            "config.file" to "$rootDir/dev.conf",
        ) + project.properties.filterKeys { it.startsWith("remocra.") || it.startsWith("log4j2") }
    }
    register<JavaExec>("run") {
        cli("serve")
    }

    cyclonedxBom {
        // includeConfigs is the list of configuration names to include when generating the BOM (leave empty to include every configuration)
        setIncludeConfigs(listOf("runtimeClasspath"))
        // Specified the type of project being built. Defaults to 'library'
        projectType = "application"
    }
}

idea {
    module {
        excludeDirs = excludeDirs + file("node_modules")
    }
}
