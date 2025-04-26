pipeline {
    agent {
        label 'linux'
    }
    stages {
        stage('Build OCI image') {
            steps {
                sh 'mvn -Pnative clean spring-boot:build-image'
            }
        }
    }
}