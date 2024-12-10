plugins {
    id("local.kotlin-base")
    kotlin("jvm")
    idea
    alias(libs.plugins.cyclonedx)
    alias(libs.plugins.licensee)
}

licensee {
    // Voir https://www.gnu.org/licenses/license-list.html pour la compatibilité avec AGPL 3.0
    // allow("AGPL-3.0-only")
    // allow("AGPL-3.0-or-later")
    allow("BSD-3-Clause")
    allow("BSD-2-Clause")
    allow("MIT")
    allow("MIT-0")
    allow("Apache-2.0")
    // allow("MPL-2.0")
    // allow("LGPL-2.1")
    allow("GPL-2.0-with-classpath-exception")
    allowUrl("http://www.jooq.org/inc/LICENSE.txt") {
        because("Dual-licensed Apache-2.0 or jOOQ License")
    }
    allowUrl("https://www.mozilla.org/en-US/MPL/2.0/") {
        because("MPL-2.0")
    }
    allowUrl("https://golang.org/LICENSE") {
        because("BSD-3-Clause")
    }
    allowUrl("http://www.eclipse.org/org/documents/edl-v10.php") {
        because("BSD-3-Clause")
    }
    allowUrl("https://asm.ow2.io/license.html") {
        because("BSD-3-Clause")
    }
    allowUrl("http://hsqldb.org/web/hsqlLicense.html") {
        because("BSD-3-Clause")
    }
    allowUrl("https://repository.jboss.org/licenses/apache-2.0.txt") {
        because("Apache-2.0")
    }
    allowUrl("https://flywaydb.org/licenses/flyway-oss") {
        because("Erreur 404 mais FlywayDB est Apache-2.0")
    }
    allowUrl("http://www.gnu.org/licenses/lgpl.html") {
        because("LGPL")
    }
    allowUrl("http://www.gnu.org/copyleft/lesser.txt") {
        because("LGPL")
    }
    allowUrl("https://github.com/geotools/geotools/blob/master/modules/plugin/epsg-hsql/LICENSE.txt") {
        because("LGPL-2.1")
    }
    allowUrl("http://jasperreports.sourceforge.net/license.html") {
        because("LGPL-3.0")
    }
    allowUrl("https://jdbc.postgresql.org/about/license.html") {
        because("BSD-2-Clause")
    }
    allowDependency("aopalliance", "aopalliance", "1.0") {
        because("Public Domain")
    }
    allowDependency("it.geosolutions.jgridshift", "jgridshift-core", "1.3") {
        because("LGPL-2.1")
    }
    allowDependency("javax.media", "jai_core", "1.1.3") {
        because("Java Distribution License")
    }
    allowDependency("org.locationtech.jts", "jts-core", "1.20.0") {
        because("Dual-licensed EPL-2.0 ou EDL-1.0 (BSD-3-Clause)")
    }
    // Dépendances javax.measurement
    allowDependency("javax.measure", "unit-api", "2.1.3") {
        because("BSD-3-Clause")
    }
    allowDependency("systems.uom", "systems-common", "2.1") {
        because("BSD-3-Clause")
    }
    allowDependency("tech.units", "indriya", "2.1.3") {
        because("BSD-3-Clause")
    }
    allowDependency("tech.uom.lib", "uom-lib-common", "2.1") {
        because("BSD-3-Clause")
    }
    // Fin des dépendances javax.measurement

    // On gère au niveau des dépendances directement plutôt que de la licence pour mieux contrôler le besoin
    ignoreDependencies("org.eclipse.emf") {
        because("EPL-1.0 n'est pas compatible avec AGPL-3.0, on ajoute une exception à la licence")
    }
}

dependencies {
    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.log4j.jul)
    implementation(libs.log4j.core)
    implementation(platform(libs.log4j.bom))
    runtimeOnly(libs.slf4j.api)
    runtimeOnly(libs.log4j.slf4j2Impl)
    runtimeOnly(libs.log4j.bom)
    runtimeOnly(libs.log4j.core)
    implementation(libs.disruptor)
    implementation(libs.sentry)
    implementation(libs.sentry.servlet.jakarta)
    implementation(libs.sentry.log4j2)

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

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.jackson)

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

    // OpenAPI
//    implementation(libs.swagger.generator)
    implementation(libs.swagger.core)
    implementation(libs.swagger.jaxrs)

    // Pour l'import des fichiers shape
    implementation(libs.geotools.gt.shapefile)

    implementation(libs.geotools.gt.main)
    implementation(libs.geotools.gt.referencing)
    implementation(libs.geotools.gt.epsg)

    // Pac4j (authn)
    implementation(libs.jakartaee.pac4j)
    implementation(libs.pac4j.oidc)
    implementation(libs.pac4j.jakartaee)

    implementation(libs.apache.poi)

    // jasper
    implementation(libs.jasperreports)
    implementation(libs.jasperreports.pdf)
    implementation(libs.jasperreports.fronts)
}

var frontendOutputDir = "$rootDir/frontend/build/parceljs"

tasks {
    fun JavaExec.cli(vararg extraArgs: String) {
        group = "cli"
        description = "Lance l'application"
        classpath = sourceSets["main"].runtimeClasspath
        mainClass = "remocra.cli.Main"
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

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(libs.versions.junit)
        }
        named<JvmTestSuite>("test") {
            dependencies {
                implementation(libs.archunit.junit5)
            }
        }
    }
}
