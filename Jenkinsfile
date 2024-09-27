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
                sh './gradlew clean test'
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
