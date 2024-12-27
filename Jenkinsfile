pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                // Mengambil kode dari repository
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
                // Membuat file JAR atau WAR
                bat 'mvn package'
            }
        }
    }
    post {
        always {
            // Menyimpan artifact hasil build
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
