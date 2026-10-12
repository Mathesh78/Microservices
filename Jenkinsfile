// pipeline {
//
//     agent any
//
//     triggers {
//         pollSCM('H/2 * * * *')
//     }
//
//     environment {
//         EC2_HOST = '13.53.197.213'
//     }
//
//     stages {
//
//         stage('Build Service Registry') {
//             steps {
//                 sh '''
//                     mvn -f service-registry/service-registry/pom.xml clean package -DskipTests
//                 '''
//             }
//         }
//
//         stage('Build API Gateway') {
//             steps {
//                 sh '''
//                     mvn -f api-gateway/api-gateway/pom.xml clean package -DskipTests
//                 '''
//             }
//         }
//
//         stage('Build Auth Service') {
//             steps {
//                 sh '''
//                     mvn -f auth-service/auth-service/pom.xml clean package -DskipTests
//                 '''
//             }
//         }
//
//         stage('Build Question Service') {
//             steps {
//                 sh '''
//                     mvn -f question-service/question-service/pom.xml clean package -DskipTests
//                 '''
//             }
//         }
//
//         stage('Build & Test Quiz Service') {
//             steps {
//                 sh '''
//                     mvn -f quiz-service/quiz-service/pom.xml clean verify
//                 '''
//             }
//         }
//
//         stage('Build Notification Service') {
//             steps {
//                 sh '''
//                     mvn -f notification-service/notification-service/pom.xml clean package -DskipTests
//                 '''
//             }
//         }
//
//         stage('Deploy to EC2') {
//
//             steps {
//
//                 withCredentials([
//                     sshUserPrivateKey(
//                         credentialsId: 'ec2-ssh-key1',
//                         keyFileVariable: 'EC2_KEY',
//                         usernameVariable: 'EC2_USER'
//                     )
//                 ]) {
//
//                     sh '''
//                         mkdir -p ~/.ssh
//                         chmod 700 ~/.ssh
//
//                         ssh \
//                             -o StrictHostKeyChecking=accept-new \
//                             -i "$EC2_KEY" \
//                             "$EC2_USER@$EC2_HOST" '
//                                 set -e
//
//                                 cd ~/Microservices
//
//                                 echo "Pulling latest code from GitHub..."
//                                 git pull --ff-only origin main
//
//                                 echo "Building and deploying containers..."
//                                 docker compose up -d --build
//
//                                 echo "Current containers:"
//                                 docker compose ps
//                             '
//                     '''
//                 }
//             }
//         }
//     }
//
//     post {
//
//         success {
//             echo 'Microservices deployed successfully to AWS EC2!'
//         }
//
//         failure {
//             echo 'Pipeline failed. Check the Jenkins logs.'
//         }
//     }
// }

pipeline {

    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

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

        stage('Docker Login') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "$DOCKERHUB_PASSWORD" | docker login \
                            -u "$DOCKERHUB_USERNAME" \
                            --password-stdin
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    sh '''
                        docker build \
                          -t "$DOCKERHUB_USERNAME/api-gateway:$IMAGE_TAG" \
                          api-gateway/api-gateway

                        docker build \
                          -t "$DOCKERHUB_USERNAME/auth-service:$IMAGE_TAG" \
                          auth-service/auth-service

                        docker build \
                          -t "$DOCKERHUB_USERNAME/question-service:$IMAGE_TAG" \
                          question-service/question-service

                        docker build \
                          -t "$DOCKERHUB_USERNAME/quiz-service:$IMAGE_TAG" \
                          quiz-service/quiz-service

                        docker build \
                          -t "$DOCKERHUB_USERNAME/notification-service:$IMAGE_TAG" \
                          notification-service/notification-service
                    '''
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    sh '''
                        docker push "$DOCKERHUB_USERNAME/api-gateway:$IMAGE_TAG"
                        docker push "$DOCKERHUB_USERNAME/auth-service:$IMAGE_TAG"
                        docker push "$DOCKERHUB_USERNAME/question-service:$IMAGE_TAG"
                        docker push "$DOCKERHUB_USERNAME/quiz-service:$IMAGE_TAG"
                        docker push "$DOCKERHUB_USERNAME/notification-service:$IMAGE_TAG"
                    '''
                }
            }
        }
        stage('Test Kubernetes Connection') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'kubernetes-token',
                        variable: 'KUBE_TOKEN'
                    )
                ]) {
                    sh '''
                        kubectl \
                          --server=https://host.docker.internal:63442 \
                          --token="$KUBE_TOKEN" \
                          --insecure-skip-tls-verify=true \
                          get deployments -n quiz-app
                    '''
                }
            }
        }
    }

    post {

        success {
            echo 'Microservice images built and pushed to Docker Hub successfully!'
        }

        failure {
            echo 'Pipeline failed. Check Jenkins logs.'
        }
    }
}