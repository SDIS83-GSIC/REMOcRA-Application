library 'atolcd-jenkins'

// Cette version doit être synchronisée :
// - avec le gradle/wrapper/gradle-wrapper.properties
// - avec les autres Jenkinsfiles
// La version dans les Jenkinsfiles n'est validée que lorsque leurs jobs sont exécutés,
// il faut donc s'assurer que la version est synchro par relecture de code lors des reviews
// (tous les Jenkinsfiles devraient être modifiés en même temps que le gradle-wrapper.properties)
def gradleImageVersion = '8.11.1-jdk21'

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
              npm run build --no-cache
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
    stage ('Generate application SBOM') {
      steps {
        nodejsInsideDocker {
          sh 'cd frontend/ && npm sbom --sbom-format cyclonedx --omit dev --sbom-type application --package-lock-only true > npm-sbom.json'
        }
      }
    }

    stage('Build and Remove docker remocra') {
      steps {
        dockerBuildAndRemove(dockerfile: 'docker/Dockerfile') { imageId ->
           dockerSbom image: imageId, file: 'docker-sbom.json', exclude: '/opt/remocra/'
         }
        dockerBuildAndRemove(buildDir: 'keycloak/')
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
