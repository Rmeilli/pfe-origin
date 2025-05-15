pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Auth Service') {
            steps {
                dir('auth-service1') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Test Auth Service') {
            steps {
                dir('auth-service1') {
                    bat 'mvn test'
                }
            }
        }
        
        stage('Build CRE Service') {
            steps {
                dir('cre-service') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Test CRE Service') {
            steps {
                dir('cre-service') {
                    bat 'mvn test'
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                dir('gestion-cre') {
                    bat 'npm install'
                    bat 'npm run build'
                }
            }
        }
        
        stage('Deploy Services') {
            steps {
                echo 'Deploying services...'
                // Ici, vous pourriez avoir des commandes pour déployer vos services
                // Par exemple, avec Docker ou Kubernetes
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed'
            // Nettoyage des ressources si nécessaire
        }
        success {
            echo 'Pipeline succeeded!'
            // Notification de succès (email, Slack, etc.)
        }
        failure {
            echo 'Pipeline failed!'
            // Notification d'échec
        }
    }
}
