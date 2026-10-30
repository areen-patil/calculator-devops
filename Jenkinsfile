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
                bat 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat "docker build -t %IMAGE% -t %IMAGE_NAME%:latest ."
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    bat 'echo %DH_PASS%| docker login -u %DH_USER% --password-stdin'
                    bat "docker push %IMAGE%"
                    bat "docker push %IMAGE_NAME%:latest"
                }
            }
            post {
                always {
                    bat 'docker logout'
                }
            }
        }

        stage('Deploy') {
            steps {
                // up -d is idempotent: unchanged services are left running
                bat 'docker compose up -d --remove-orphans'
                powershell '''
                    $ok = $false
                    for ($i = 1; $i -le 20; $i++) {
                        try {
                            $r = Invoke-RestMethod -Uri http://localhost:8081/actuator/health -TimeoutSec 5
                            if ($r.status -eq 'UP') { $ok = $true; break }
                        } catch {}
                        Start-Sleep -Seconds 3
                    }
                    if (-not $ok) { Write-Error 'Health check failed'; exit 1 }
                    Write-Output 'Deployment healthy'
                '''
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
