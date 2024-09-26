pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                sh './gradlew clean test'
            }
        }
        stage('Check code coverage') {
            steps {
                sh './gradlew clean jacocoTestCoverageVerification'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean nativeCompile'
            }
        }
        stage('Deploy') {
            when {
                branch "trunk"
            }
            steps {
                sh './build/native/nativeCompile/ZeroDay'
            }
        }
    }
}
