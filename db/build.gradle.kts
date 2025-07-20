import net.ltgt.gradle.flyway.tasks.FlywayMigrate

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

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
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
    spotlessKotlin {
        mustRunAfter(jooq)
    }
}

spotless {
    format("xml") {
        target("src/**/*.xml")
    }
}
