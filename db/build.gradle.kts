import org.flywaydb.gradle.task.FlywayMigrateTask
import java.util.Properties

buildscript {
    dependencies {
        classpath(libs.flyway.database.postgresql) { because("Requis par le plugin Flyway") }
        classpath(libs.postgresql) { because("Requis par le plugin Flyway") }
    }
}

plugins {
    id("local.kotlin-base")
    kotlin("jvm")
    idea
    alias(libs.plugins.flyway)
}

val flywayConf by lazy {
    Properties().apply {
        file("src/main/resources/db/flyway.conf").reader().use { load(it) }
    }
}

val dbUrl = providers.gradleProperty("db.url").getOrElse("jdbc:postgresql://localhost:5432/remocra")
val dbUser = providers.gradleProperty("db.user").getOrElse("remocra")
val dbPassword = providers.gradleProperty("db.password").getOrElse("remocra")

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
    schemas = flywayConf.getProperty("flyway.schemas").split(',').toTypedArray()
    cleanDisabled = flywayConf.getProperty("flyway.cleanDisabled").toBoolean()
}

val jooqCodegen: Configuration by configurations.creating

dependencies {
    api(libs.jooq.kotlin)
    api(libs.jooq.codegen)

    api(libs.guava)
    implementation(libs.geotools.gt.main)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.flyway.database.postgresql)

    jooqCodegen(libs.jooq.codegen)
    jooqCodegen(libs.postgresql)
}

val jooqOutputDir = file("src/main/jooq")

tasks {
    val flywayMigrate by existing

    register<FlywayMigrateTask>("flywayMigrateData") {
        table = "flyway_data_history"
        locations = arrayOf("filesystem:${file("src/data")}")
        baselineOnMigrate = true
        baselineVersion = "0"

        dependsOn(flywayMigrate)
    }
    val jooq by registering(JavaExec::class) {
        val jooqConfigFile = file("src/jooq-codegen.xml")

        dependsOn(flywayMigrate)

        inputs.dir("src/main/resources/db/migration").withPathSensitivity(PathSensitivity.RELATIVE)
        inputs.file(jooqConfigFile).withPathSensitivity(PathSensitivity.NONE)
        outputs.dir(jooqOutputDir)

        doFirst {
            project.delete(jooqOutputDir)
        }
        classpath = jooqCodegen
        classpath += sourceSets["main"].runtimeClasspath
        mainClass = "org.jooq.codegen.GenerationTool"
        systemProperties = mapOf(
            "db.url" to dbUrl,
            "db.user" to dbUser,
            "db.password" to dbPassword,
            "outputdir" to jooqOutputDir.path,
        )
        args(jooqConfigFile)
        dependsOn(spotlessKotlin)
        finalizedBy(spotlessKotlinApply)
    }
}
sourceSets {
    main {
        java {
            srcDir(jooqOutputDir)
        }
    }
}
idea {
    module {
        generatedSourceDirs.add(jooqOutputDir)
    }
}

spotless {
    format("xml") {
        target("src/**/*.xml")
    }
}
