pipeline {
    agent any
    environment {
        SONAR_HOME = tool "Sonar"
    }
    stages {
        stage("Clean Workspace") {
            steps {
                cleanWs()
            }
        }
        stage("Code Clone") {
            steps {
                git url: "https://github.com/pundir8372/DevOps-mega-project.git", branch: "project"
            }
        }
        stage("SonarQube Quality Analysis") {
            steps {
                withSonarQubeEnv("Sonar") {
                    sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=bankapp -Dsonar.projectKey=bankapp -Dsonar.java.binaries=. -X"
                }
            }
        }
        stage("OWASP Dependency Check") {
            steps {
                dependencyCheck additionalArguments: "--scan ./" , odcInstallation: 'dc'
                dependencyCheckPublisher pattern: "**/dependency-check-report.xml"
            }
        }
        stage("Sonar Quality Gate Scan") {
            steps {
                timeout(time: 3, unit: "MINUTES") {
                    waitForQualityGate abortPipeline: false
                }
            }
        }
        stage("Trivy File System Scan") {
            steps {
                sh "trivy fs --format table -o table-report.html ."
            }
        }
        stage("Docker Build") {
            steps {
                sh "docker build -t bankapp:latest ."
            }
        }
        stage("Push to Docker Hub") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerHubCred', passwordVariable: 'dockerHubPass', usernameVariable: 'dockerHubUser')]) {
                    sh " docker login -u ${dockerHubUser} -p ${dockerHubPass}"
                    sh  "docker tag bankapp ${dockerHubUser}/bankapp:latest"
                    sh    "docker push ${dockerHubUser}/bankapp:latest"
    
                }
            }
        }
        
    }
    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline executed successfully!"
        }
        failure {
            echo "Pipeline failed. Please check the logs."
        }
    }
}
