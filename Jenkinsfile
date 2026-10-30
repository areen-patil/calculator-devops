pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'your-dockerhub-username'   // TODO: set
        NOTIFY_EMAIL   = 'your-email@example.com'    // TODO: set
        IMAGE_NAME     = "${DOCKERHUB_USER}/calculator-api"
        IMAGE          = "${IMAGE_NAME}:${BUILD_NUMBER}"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t "$IMAGE" -t "$IMAGE_NAME:latest" .'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    sh 'docker push "$IMAGE"'
                    sh 'docker push "$IMAGE_NAME:latest"'
                }
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }

        stage('Deploy') {
            steps {
                // up -d is idempotent: unchanged services are left running.
                // --wait blocks until every service reports healthy, and fails otherwise.
                sh 'docker compose up -d --build --remove-orphans --wait --wait-timeout 120'
                sh 'docker compose ps'
            }
        }
    }

    post {
        success {
            mail to: "${NOTIFY_EMAIL}",
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build succeeded and deployed.\n${env.BUILD_URL}"
        }
        failure {
            mail to: "${NOTIFY_EMAIL}",
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build failed.\n${env.BUILD_URL}console"
        }
    }
}
