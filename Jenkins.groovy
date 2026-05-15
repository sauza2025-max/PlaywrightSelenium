pipeline {
agent any
tools {
// Install the Maven version configured as "M3" and add it to the path.
maven "Maven 3.9"
}
environment {
SONARQUBE_SERVER = 'SonarQube' // SonarQube server configured in Jenkins
}
stages {
stage('Build') {
steps {
// Get some code from a GitHub repository
git 'https://github.com/sauza2025-max/PlaywrightSelenium.git'
// Run Maven on a Unix agent.
//sh "mvn -Dmaven.test.failure.ignore=true clean package"
// To run Maven on a Windows agent, use
bat "mvn -Dmaven.test.failure.ignore=true clean package"
}
}
stage('Run Unit Tests') { // Executes unit tests
steps {
bat 'mvn test'
}
}

    }

            post {
                always {
                    // Publish test results using a robust glob pattern
                    junit '**/target/surefire-reports/*.xml'
                    allure([
    includeProperties: false,
    jdk: '',
    results: [[path: 'target/allure-results']]
])
                    recordCoverage(
                            tools: [[
                                            parser: 'JACOCO',
                                            pattern: 'target/site/jacoco/jacoco.xml'
                                    ]]
                    )
                }
// If Maven was able to run the tests, even if some of the test
// failed, record the test results and archive the jar file.
success {
junit '**/target/surefire-reports/TEST-*.xml'
archiveArtifacts 'target/*.jar'
}
}
}

