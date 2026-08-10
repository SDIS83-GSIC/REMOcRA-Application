import net.ltgt.gradle.flyway.tasks.FlywayMigrate
import net.ltgt.gradle.jooq.tasks.JooqCodegen
import java.sql.DriverManager

plugins {
    id("local.kotlin-base")
    kotlin("jvm")
    idea
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq)
}

val dbUrl = providers.gradleProperty("db.url").orElse("jdbc:postgresql://localhost:5432/remocra")
val dbUser = providers.gradleProperty("db.user").orElse("remocra")
val dbPassword = providers.gradleProperty("db.password").orElse("remocra")

val dbBaseUrl = dbUrl.get().substringBeforeLast("/")
val dbJooqStandaloneName = "jooq_gen"

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
}

buildscript {
    dependencies {
        classpath(libs.postgresql)
        classpath(libs.flyway.database.postgresql)
    }
}

dependencies {
    api(libs.jooq.kotlin)

    api(libs.guava)
    implementation(libs.locationtech.jts.core)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.flyway.database.postgresql)

    flyway(libs.postgresql)
    flyway(libs.flyway.core)
    flyway(libs.flyway.database.postgresql)

    jooqCodegen(libs.jooq.codegen)
    jooqCodegen(libs.postgresql)
}

tasks {
    register<FlywayMigrate>("flywayMigrateData") {
        migrationLocations.setFrom(file("src/data"))

        configuration.put("flyway.table", "flyway_data_history")
        configuration.put("flyway.baselineOnMigrate", "true")
        configuration.put("flyway.baselineVersion", "0")

        dependsOn(flywayMigrate)
    }
    jooq {
        dependsOn(flywayMigrate)
        finalizedBy(spotlessKotlinApply)

        url = dbUrl
        user = dbUser
        password = dbPassword
    }

    val initStandalone = register("initStandalone") {
        description = "Initialise la base de données standalone"

        doFirst {
            // création de la base de données
            DriverManager
                .getConnection(
                    "$dbBaseUrl/postgres",
                    dbUser.get(),
                    dbPassword.get(),
                ).use { conn ->
                    conn
                        .prepareStatement(
                            """
                                DROP DATABASE IF EXISTS $dbJooqStandaloneName WITH (FORCE);;
                                CREATE DATABASE $dbJooqStandaloneName;
                            """,
                        ).execute()
                }
            // création de l'extension postgis
            DriverManager
                .getConnection(
                    "$dbBaseUrl/$dbJooqStandaloneName",
                    dbUser.get(),
                    dbPassword.get(),
                ).use { conn ->
                    conn
                        .prepareStatement("CREATE EXTENSION IF NOT EXISTS postgis;")
                        .execute()
                }
        }
    }

    val dropStandalone = register("dropStandalone") {
        description = "Supprime la base de données standalone"

        doFirst {
            // Suppression de la base de données
            DriverManager
                .getConnection(
                    "$dbBaseUrl/postgres",
                    dbUser.get(),
                    dbPassword.get(),
                ).use { conn ->
                    conn.prepareStatement("DROP DATABASE $dbJooqStandaloneName;").execute()
                }
        }
    }

    val flywayMigrateStandalone = register<FlywayMigrate>("flywayMigrateStandalone") {
        description = "Migre la base de données standalone"
        url = "$dbBaseUrl/$dbJooqStandaloneName"

        dependsOn(initStandalone)
    }

    val jooqStandalone = register<JooqCodegen>("jooqStandalone") {
        description = "Génère les fichiers jOOQ en fonction d'une base crée (puis supprimé) spécifiquement pour ça."
        dependsOn(flywayMigrateStandalone)
        finalizedBy(spotlessKotlinApply, dropStandalone)

        configurationFile.set(jooq.flatMap { it.configurationFile })
        outputDirectory.set(jooq.flatMap { it.outputDirectory })
        classpath.from(configurations.named("jooqCodegenClasspath"))

        url = "$dbBaseUrl/$dbJooqStandaloneName"
        user = dbUser
        password = dbPassword
    }

    spotlessKotlin {
        mustRunAfter(jooq, jooqStandalone)
    }
}

spotless {
    format("xml") {
        target("src/**/*.xml")
    }
}
