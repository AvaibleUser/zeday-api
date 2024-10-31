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
        }
        stage('Code Coverage') {
            steps {
                sh './gradlew jacocoTestCoverageVerification'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean build'
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
