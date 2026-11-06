pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
        DOCKER_HUB_USER = "akashv2310"
        IMAGE_NAME = "${DOCKER_HUB_USER}/cloud-registration"
        BUILD_TAG = "${env.BUILD_NUMBER}"
        AZURE_RG = "cloud-devops-rg"
        AZURE_APP_NAME = "akash-cloud-reg"
    }

    stages {
        stage('Checkout Source') {
            steps { 
                checkout scm 
            }
        }

        stage('Secret Detection (Gitleaks)') {
            steps {
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
                // Compile explicitly for linux/amd64 architecture
                sh "docker build --platform linux/amd64 -t ${IMAGE_NAME}:${BUILD_TAG} -t ${IMAGE_NAME}:latest ."
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

        stage('Deploy to Azure App Service') {
            steps {
                sh '''
                # Instruct Azure to pull the new build and ensure ingress is routed to port 8080
                az webapp config appsettings set --resource-group "$AZURE_RG" --name "$AZURE_APP_NAME" --settings WEBSITES_PORT=8080
                az webapp config container set --resource-group "$AZURE_RG" --name "$AZURE_APP_NAME" --docker-custom-image-name "docker.io/${IMAGE_NAME}:${BUILD_TAG}"
                az webapp restart --resource-group "$AZURE_RG" --name "$AZURE_APP_NAME"
                '''
            }
        }

        stage('Verify Availability') {
            steps {
                sh '''
                echo "Sleeping 20 seconds for container startup..."
                sleep 20
                curl -I "https://${AZURE_APP_NAME}.azurewebsites.net"
                curl -i "https://${AZURE_APP_NAME}.azurewebsites.net/health"
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
        }
        success {
            echo "CI/CD Pipeline Successful! Service is live at: https://${AZURE_APP_NAME}.azurewebsites.net"
        }
    }
}