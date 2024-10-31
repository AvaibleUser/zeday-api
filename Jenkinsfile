pipeline {
    agent any

    stages {
        stage('Enviroments') {
            steps {
                sh """cp /var/jenkins_home/workspace/.env ${env.WORKSPACE}"""
            }
        }
        stage('Test') {
            steps {
                sh './gradlew cleanTest test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/test-results/*.xml'
                    recordCoverage(
                            tools: [[parser: 'JACOCO', pattern: '**/jacoco/test/*.xml']],
                            id: 'jacoco',
                            name: 'JaCoCo Coverage',
                            sourceCodeRetention: 'EVERY_BUILD',
                            qualityGates: [
                                    [threshold: 80.0, metric: 'LINE', baseline: 'PROJECT'],
                                    [threshold: 80.0, metric: 'BRANCH', baseline: 'PROJECT']])
                }
            }
        }
        stage('Code Coverage') {
            steps {
                sh './gradlew jacocoTestCoverageVerification'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean package'
            }
        }
        stage('Deploy') {
            when {
                branch "trunk"
            }
            steps {
                script {
                    def jarFiles = sh(script: 'ls /build/libs/*.jar', returnStdout: true).trim().split('\n')

                    def jarToDeploy = jarFiles.find { jar -> 
                        jar.endsWith('.jar') && !jar.endsWith('-plain.jar')
                    }

                    if (jarToDeploy) {
                        sh "java -jar ${jarToDeploy}"
                    } else {
                        error("No suitable JAR file found for deployment.")
                    }
                }
            }
        }
    }
}
