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
    // allow("BSD-2-Clause")
    allow("MIT")
    allow("MIT-0")
    allow("Apache-2.0")
    // allow("MPL-2.0")
    // allow("LGPL-2.1")
    allow("GPL-2.0-with-classpath-exception")
    allowUrl("https://www.jooq.org/inc/LICENSE.txt") {
        because("Dual-licensed Apache-2.0 or jOOQ License")
    }
    allowUrl("https://opensource.org/license/mit") {
        because("MIT")
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
    allowUrl("http://www.gnu.org/copyleft/lesser.txt") {
        because("LGPL")
    }
    allowUrl("https://github.com/geotools/geotools/blob/master/modules/plugin/epsg-hsql/LICENSE.txt") {
        because("LGPL-2.1")
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
    allowDependency("org.locationtech.jts.io", "jts-io-common", "1.20.0") {
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
    allowDependency("oro", "oro", "2.0.8") {
        because("Apache-2.0")
    }
    // Fin des dépendances javax.measurement

    // On gère au niveau des dépendances directement plutôt que de la licence pour mieux contrôler le besoin
    ignoreDependencies("org.eclipse.emf") {
        because("EPL-1.0 n'est pas compatible avec AGPL-3.0, on ajoute une exception à la licence")
    }
    ignoreDependencies("com.lowagie") {
        because("MPL-1.1 n'est pas compatible avec AGPL-3.0, on ajoute une exception à la licence")
    }

    allowDependency("org.json", "json", "20250517") {
        because("Public Domain")
    }
}

dependencies {
    // Logging
    implementation(libs.slf4j.api)
    implementation(libs.log4j.jul)
    implementation(libs.log4j.core)
    implementation(platform(libs.log4j.bom))
    implementation(libs.json)
    runtimeOnly(libs.log4j.slf4j2Impl)
    runtimeOnly(libs.disruptor)
    api(libs.sentry)
    api(libs.sentry.servlet.jakarta)
    api(libs.sentry.log4j2)

    // Utilitaires
    compileOnly(libs.forbiddenapis)
    api(libs.typesafe)
    api(libs.picocli)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlin.reflect)
    api(libs.commons.email)
    api(libs.caffeine)

    // Dependency Injection
    api(libs.guice.bom)
    api(libs.kotlin.guice)

    // Base de données
    api(projects.db)
    api(libs.hikaricp)
    api(libs.jooq.kotlin)
    runtimeOnly(libs.postgresql)
    api(libs.flyway.core)

    // Web
    api(libs.jetty.servlet)
    api(libs.jetty.servlets)
    api(platform(libs.jetty.bom))
    api(platform(libs.jetty.ee10.bom))

    api(libs.oidcServlets)
    api(libs.oidcServlets.rs)
    api(platform(libs.oidcServlets.bom))
    api(libs.oauthServlets.rs)
    api(platform(libs.oauthServlets.bom))

    api(libs.retrofit)
    api(libs.retrofit.converter.jackson)

    api(libs.resteasy.core.spi)
    api(libs.resteasy.core)
    api(platform(libs.resteasy.bom))

    // JSON
    api(libs.jackson.jakarta.rs.json.provider)
    api(libs.jackson.module.kotlin)
    api(libs.jackson.datatype.guava)
    api(libs.jackson.datatype.jdk8)
    api(libs.jackson.datatype.jsr310)
    api(libs.jackson.dataformat.csv)

    // OpenAPI
    runtimeOnly(libs.swagger.ui)
    api(libs.swagger.core)
    api(libs.swagger.jaxrs)

    // Pour l'import des fichiers shape
    api(libs.geotools.gt.shapefile)

    api(libs.geotools.gt.main)
    api(libs.geotools.gt.referencing)
    api(libs.geotools.gt.epsg)
    api(libs.locationtech.jts.io)

    api(libs.apache.poi)

    api(libs.owasp)

    api(libs.xdocreport)
    api(libs.xdocreport.odt)
    api(libs.xdocreport.freemarker)
    api(libs.xdocreport.velocity)
    api(libs.xdocreport.converter)
    api(libs.xdocreport.odfdom)
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
            "remocra.http.relax-csp-for-development" to "true",
            "remocra.http.static-dir" to frontendOutputDir,
            "remocra.http.session-store-dir" to "${project.layout.buildDirectory.asFile.get()}/session",
            "remocra.fs.base-dir" to "$rootDir/.data/",
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
        register<JvmTestSuite>("pgTest") {
            dependencies {
                implementation(project())
            }
            targets.configureEach {
                testTask {
                    systemProperties = mapOf(
                        "config.file" to "$rootDir/dev.conf",
                    ) + project.properties.filterKeys { it.startsWith("remocra.") }
                }
            }
        }
    }
}
