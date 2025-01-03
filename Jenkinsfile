pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/DuckOfTheBooBoo/FlowManageMaven.git'
            }
        }
        stage('Test') {
            steps {
                withMaven(maven: 'Maven3', traceability: true) {
                    bat 'mvn test'
                    bat 'mvn jacoco:report'
                }
            }
        }
        stage('Build Artifact') {
            steps {
                withMaven(maven: 'Maven3', traceability: true) {
                    bat 'mvn package'
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                echo 'Building Docker Compose services...'
                bat 'docker compose build --no-cache'
                bat 'docker images' // Debugging images
            }
        }
        stage('Deploy') {
            steps {
                echo 'Starting Docker Compose services...'
                bat 'docker compose up -d --build'
                bat 'docker ps' // Debugging running containers
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
            recordCoverage qualityGates: [[criticality: 'NOTE', integerThreshold: 73, metric: 'MODULE', threshold: 73.0]], tools: [[parser: 'JACOCO']]
        }
        success {
            echo 'Build berhasil!'
        }
        failure {
            echo 'Build gagal!'
        }
    }
}
