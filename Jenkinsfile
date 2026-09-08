pipeline {

    agent any

    triggers {
            pollSCM('H/2 * * * *')
        }

    environment {
        RABBITMQ_USER = 'quizuser'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build Service Registry') {
            steps {
                sh '''
                    mvn -f service-registry/service-registry/pom.xml clean package -DskipTests
                '''
            }
        }

        stage('Build API Gateway') {
            steps {
                sh '''
                    mvn -f api-gateway/api-gateway/pom.xml clean package -DskipTests
                '''
            }
        }

        stage('Build Auth Service') {
            steps {
                sh '''
                    mvn -f auth-service/auth-service/pom.xml clean package -DskipTests
                '''
            }
        }

        stage('Build Question Service') {
            steps {
                sh '''
                    mvn -f question-service/question-service/pom.xml clean package -DskipTests
                '''
            }
        }

        stage('Build Quiz Service') {
            steps {
                sh '''
                    mvn -f quiz-service/quiz-service/pom.xml clean package -DskipTests
                '''
            }
        }

        stage('Build Notification Service') {
            steps {
                sh '''
                    mvn -f notification-service/notification-service/pom.xml clean package -DskipTests
                '''
            }
        }

        stage('Deploy') {

            steps {

                withCredentials([

                    string(
                        credentialsId: 'mysql-db-password',
                        variable: 'DB_PASSWORD'
                    ),

                    string(
                        credentialsId: 'jwt-secret',
                        variable: 'JWT_SECRET'
                    ),

                    string(
                        credentialsId: 'rabbitmq-password',
                        variable: 'RABBITMQ_PASSWORD'
                    )

                ]) {

                    sh '''
                        docker compose -p microservices up -d --build
                    '''

                }
            }
        }

        stage('Check Containers') {
            steps {
                sh '''
                    docker compose -p microservices ps
                '''
            }
        }
    }

    post {

        success {
            echo 'Microservices deployed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the Jenkins logs.'
        }
    }
}