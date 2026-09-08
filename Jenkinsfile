pipeline {

    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        EC2_HOST = '13.53.197.213'
    }

    stages {

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

        stage('Build & Test Quiz Service') {
            steps {
                sh '''
                    mvn -f quiz-service/quiz-service/pom.xml clean verify
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

        stage('Deploy to EC2') {

            steps {

                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'ec2-ssh-key1',
                        keyFileVariable: 'EC2_KEY',
                        usernameVariable: 'EC2_USER'
                    )
                ]) {

                    sh '''
                        mkdir -p ~/.ssh
                        chmod 700 ~/.ssh

                        ssh \
                            -o StrictHostKeyChecking=accept-new \
                            -i "$EC2_KEY" \
                            "$EC2_USER@$EC2_HOST" '
                                set -e

                                cd ~/Microservices

                                echo "Pulling latest code from GitHub..."
                                git pull --ff-only origin main

                                echo "Building and deploying containers..."
                                docker compose up -d --build

                                echo "Current containers:"
                                docker compose ps
                            '
                    '''
                }
            }
        }
    }

    post {

        success {
            echo 'Microservices deployed successfully to AWS EC2!'
        }

        failure {
            echo 'Pipeline failed. Check the Jenkins logs.'
        }
    }
}