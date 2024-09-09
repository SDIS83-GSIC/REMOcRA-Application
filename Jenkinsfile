library 'atolcd-jenkins'
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
      }
    }
    stage('Build Gradle') {
      steps {
        withSidecarContainers(
          pg: [ imageName: 'postgis/postgis', imageVersion: '16-3.4', args: '-e "POSTGRES_DB=remocra" -e "POSTGRES_USER=remocra" -e "POSTGRES_PASSWORD=remocra"' ]
        ) {
          gradleInsideDocker(imageVersion: '8.6-jdk21') {
            sh '''
                gradle --stacktrace build
                gradle --stacktrace cyclonedxBom
                gradle --stacktrace flywayMigrateData -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
                gradle --stacktrace pgTest -Premocra.database.dataSource.serverName=pg -Pdb.url=jdbc:postgresql://pg/remocra -Pdb.user=remocra -Pdb.password=remocra
              '''
          }
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
        smartDependencyTrackPublisher name: 'remocre-v3',
          version: env.BRANCH_NAME,
          bomFiles: ['docker-sbom.json', 'frontend/npm-sbom.json', 'app/build/reports/bom.json'],
          classifier: 'APPLICATION',
          hierarchical: true
      }
    }
  }
}
