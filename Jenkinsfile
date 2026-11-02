pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
        DOCKER_HUB_USER = "akashv2310"
        IMAGE_NAME = "${DOCKER_HUB_USER}/cloud-registration"
        BUILD_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Secret Detection (Gitleaks)') {
            steps {
                echo 'Running Gitleaks secret detection...'
                sh 'gitleaks detect --source . --verbose --redact'
            }
        }

        stage('Compile & Test with Maven') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Package Application') {
            steps {
                sh 'mvn package -DskipTests=true'
            }
        }

        stage('Build & Tag Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${BUILD_TAG} ."
                sh "docker tag ${IMAGE_NAME}:${BUILD_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Publish Image to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    sh "docker push ${IMAGE_NAME}:${BUILD_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy via Ansible to Kubernetes') {
            steps {
                dir('ansible') {
                    sh 'ansible-playbook -i inventory.ini deploy.yml'
                }
            }
        }

        stage('Verify Availability') {
            steps {
                sh 'kubectl get pods -l app=cloud-registration'
                sh 'kubectl get svc cloud-registration-service'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
        }
        success {
            echo "CI/CD & Security Checks Passed Successfully. Application is Live."
        }
        failure {
            echo "Pipeline Failed. Deployment halted."
        }
    }
}