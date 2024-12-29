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
        stage('Package') {
            steps {
                bat 'mvn package'
            }
        }
        stage('Docker Compose Build') {
            steps {
                echo 'Building Docker Compose services...'
                bat 'docker compose build --no-cache'
                bat 'docker images' // Debugging images
            }
        }
        stage('Docker Compose Up') {
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
        }
        success {
            echo 'Build berhasil!'
        }
        failure {
            echo 'Build gagal!'
        }
    }
}
