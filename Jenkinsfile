library 'atolcd-jenkins'

// Cette version doit être synchronisée :
// - avec le gradle/wrapper/gradle-wrapper.properties
// - avec les autres Jenkinsfiles
// La version dans les Jenkinsfiles n'est validée que lorsque leurs jobs sont exécutés,
// il faut donc s'assurer que la version est synchro par relecture de code lors des reviews
// (tous les Jenkinsfiles devraient être modifiés en même temps que le gradle-wrapper.properties)
def gradleImageVersion = '8.12.0-jdk21'

pipeline {
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }
  agent any
  stages {
    stage('Validate Jenkinsfiles') {
      steps {
        validateJenkinsfiles 'Jenkinsfile-*'

        gradleInsideDocker(imageVersion: gradleImageVersion) {
          sh 'gradle wrapper'
        }
        verifyUnmodified('gradle/wrapper/gradle-wrapper.properties') {
          setGerritReview unsuccessfulMessage: 'La version de Gradle a été mise à jour mais les Jenkinsfiles ne la reflètent pas'
        }
      }
      post {
        unsuccessful {
          sh 'git restore -- gradlew* gradle/wrapper/'
        }
      }
    }
    stage('Validate scripts & Dockerfiles') {
      steps {
        insideDocker(imageName: 'koalaman/shellcheck-alpine', imageVersion:'stable') {
          sh '''
            find . -not \\( -name node_modules -prune \\) -name "*.sh" -exec shellcheck '{}' +
            '''
        }
        insideDocker(imageName: 'hadolint/hadolint', imageVersion:'latest-alpine') {
          sh '''
            find . -not \\( -name node_modules -prune \\) -name Dockerfile -exec hadolint '{}' +
            '''
        }
      }
    }
    stage('Build AsciiDoc documentation') {
        steps {
            insideDocker(imageName: 'docker-registry.priv.atolcd.com/asciidoctor') {
                sh '/entrypoint.sh pdf atolcd --destination-dir doc/build/ doc/*.adoc'
                sh '/entrypoint.sh html --destination-dir doc/build/ --attribute data-uri doc/*.adoc'
            }
            publishDoc dirs: 'doc/build', remoteDir: 'docs'
        }
        post {
            cleanup {
                sh 'rm doc/build/*'
            }
        }
    }
    stage('Build Gradle') {
      steps {
        withSidecarContainers(
          pg: [ imageName: 'postgis/postgis', imageVersion: '16-3.4', args: '-e "POSTGRES_DB=remocra" -e "POSTGRES_USER=remocra" -e "POSTGRES_PASSWORD=remocra"' ]
        ) {
          gradleInsideDocker(imageVersion: gradleImageVersion) {
            sh '''
                gradle --stacktrace build
                gradle --stacktrace cyclonedxBom
                # Exécute jooq *après* build pour ne pas reformater les src/main/kotlin et avoir spotlessCheck qui passe sans erreur alors qu'il ne devrait pas
                gradle --stacktrace jooq -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
                gradle --stacktrace flywayMigrateData -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
                gradle --stacktrace pgTest -Premocra.database.dataSource.serverName=pg -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
                # On s'assure que flywayClean fonctionne les migrations sont utilisables après coup
                gradle --stacktrace flywayClean -Pflyway.cleanDisabled=false -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
                gradle --stacktrace flywayMigrateData -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
              '''
          }
          verifyUnmodified('db/src/main/jooq/') {
            setGerritReview unsuccessfulMessage: "La génération jOOQ n'est pas à jour par rapport aux migrations FlywayDB"
          }
        }
      }
      post {
        unsuccessful {
          sh 'git restore -- db/src/main/jooq/'
        }
      }
    }
    stage('Build npm') {
      steps {
        nodejsInsideDocker() {
          dir(path: 'frontend/') {
            sh '''
              npm ci
              npm run lint
              npm run build -- --no-cache
              npx @cyclonedx/cyclonedx-npm --omit dev --package-lock-only > npm-sbom.json
              '''
          }
        }
      }
      post {
        always {
          dir(path: 'frontend/') {
            sh '''
              rm -rf node_modules/
              '''
          }
        }
      }
    }

    stage('Build docker') {
      parallel {
        stage('Build and Remove docker remocra') {
          steps {
            dockerBuildAndRemove(dockerfile: 'docker/Dockerfile') { imageId ->
                dockerSbom image: imageId, file: 'docker-sbom.json', exclude: '/opt/remocra/'
                dockerDive image: imageId
            }
          }
        }

        stage('Build and Remove docker keycloak') {
          when {
            expression { !isGerritReview() || headChangeset('keycloak/**') || headChangeset('Jenkinsfile') }
          }
          steps {
            dockerBuildAndRemove(buildDir: 'keycloak/') { imageId ->
              withSidecarContainers([
                keycloak: [ imageId: imageId, args: ' -e KC_DB=dev-file -e KC_BOOTSTRAP_ADMIN_USERNAME=kcadmin -e KC_BOOTSTRAP_ADMIN_PASSWORD=kcadmin', command: 'start-dev' ],
              ]) {
                insideDocker(imageId: imageId, runExtraParams: '-e KEYCLOAK_URL=http://keycloak:8080 -e KEYCLOAK_USER=kcadmin -e KEYCLOAK_PASSWORD=kcadmin '
                    // configuration du realm "remocra"
                    + '-e CLIENT_SECRET=remocra -e REMOCRA_URL=http://remocra:8881') {
                  sh '/entrypoint.sh keycloak-config-cli'
                }
              }
            }
          }
        }

        stage ('Build and Remove docker geoserver') {
          when {
            expression { !isGerritReview() || headChangeset('geoserver/**') || headChangeset('Jenkinsfile') }
          }
          steps {
            dockerBuildAndRemove(buildDir: 'geoserver') { imageId ->
              dockerDive image: imageId
              withSidecarContainers([
                geoserver: [ imageId: imageId ],
              ]) {
                insideDocker(imageId: imageId, runExtraParams: '-e GEOSERVER_URL=http://geoserver:8090/geoserver -e GEOSERVER_USER=admin -e GEOSERVER_PASSWORD=geoserver '
                    + '-e POSTGIS_HOSTNAME=db -e POSTGIS_USER=remocra -e POSTGIS_PASSWORD=remocra') {
                  sh '/entrypoint.sh load-data'
                }
              }
            }
          }
        }

        stage ('Build and Remove docker apache hop') {
          when {
            expression { !isGerritReview() || headChangeset('apachehop/**') || headChangeset('Jenkinsfile') }
          }
          steps {
            dockerBuildAndRemove(buildDir: 'apachehop') { imageId ->
              dockerDive image: imageId
            }
          }
        }
      }
    }

    stage ('Publish SBOM') {
      when {
        expression { ! isGerritReview() }
      }
      steps {
        smartDependencyTrackPublisher name: 'remocra-v3',
          version: env.BRANCH_NAME,
          bomFiles: ['docker-sbom.json', 'frontend/npm-sbom.json', 'app/build/reports/bom.json'],
          classifier: 'APPLICATION',
          hierarchical: true
      }
    }
  }
}
