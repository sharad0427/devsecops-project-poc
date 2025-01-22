# Deploy Springboot BankApp using Jenkins - DevSecOps Project!

### **Phase 1: Initial Setup and Deployment**

**Step 1: Launch EC2 (Ubuntu 22.04):**

- AWS EC2 instance (Ubuntu) with instance type t2.large and root  volume 29GB
- Connect to the instance using SSH.

**Step 2: Clone the Code:**

- Update all the packages and then clone the code.
- Clone your application's code repository onto the EC2 instance:
    
    ```bash
    git clone https://github.com/pundir8372/DevOps-mega-project.git
    ```
    

**Step 3: Install Docker and docker-compose:**

- Set up Docker on the EC2 instance:
    
    ```bash
    
    sudo apt-get update
    sudo apt-get install docker.io -y
    sudo usermod -aG docker $USER  # Replace with your system's username, e.g., 'ubuntu'
    newgrp docker
    sudo chmod 777 /var/run/docker.sock
    ```

-  Install Docker Compose 

      ```bash

      sudo apt-get install docker-compose-v2
      ```   
    

**Phase 2: Security**

1. **Install SonarQube and Trivy:**
    - Install SonarQube and Trivy on the EC2 instance to scan for vulnerabilities.
        
        sonarqube
        ```
        docker run -d --name sonar -p 9000:9000 sonarqube:lts-community
        ```
        
        
        To access: 
        
        publicIP:9000 (by default username & password is admin)
        
        To install Trivy:
        ```
        sudo apt-get install wget apt-transport-https gnupg lsb-release
        wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
        echo deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main | sudo tee -a /etc/apt/sources.list.d/trivy.list
        sudo apt-get update
        sudo apt-get install trivy        
        ```
        
        to scan image using trivy
        ```
        trivy image <imageid>
        ```
        
        
2. **Integrate SonarQube and Configure:**
    - Integrate SonarQube with your CI/CD pipeline.
    - Configure SonarQube to analyze code for quality and security issues.

**Phase 3: CI/CD Setup**

1. **Install Jenkins for Automation:**
    - Install Jenkins on the EC2 instance to automate deployment:
    Install Java
    
    ```bash
    sudo apt update
    sudo apt install fontconfig openjdk-17-jre
    java -version
    
    
    #jenkins
    sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
    https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
    echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
    https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
    /etc/apt/sources.list.d/jenkins.list > /dev/null
    sudo apt-get update
    sudo apt-get install jenkins
    sudo systemctl start jenkins
    sudo systemctl enable jenkins
    ```
    
    - Access Jenkins in a web browser using the public IP of your EC2 instance.
        
        publicIp:8080
        
2. **Install Necessary Plugins in Jenkins:**

Go to Manage Jenkins →Plugins → Available Plugins →

Install below plugins

1 OWASP

2 SonarQube Scanner

3 Docker

4 Pipeline Stage View




### SonarQube

Create the token

Go to Jenkins Dashboard → Manage Jenkins → Credentials → Add Secret Text. It should look like this

After adding sonar token

Click on Apply and Save

**The Configure System option** is used in Jenkins to configure different server

**Global Tool Configuration** is used to configure different tools that we install using Plugins

We will install a sonar scanner in the tools.


**Install Dependency-Check and Docker Tools in Jenkins**

**Install Dependency-Check Plugin:**

- Go to "Dashboard" in your Jenkins web interface.
- Navigate to "Manage Jenkins" → "Manage Plugins."
- Click on the "Available" tab and search for "OWASP Dependency-Check."
- Check the checkbox for "OWASP Dependency-Check" and click on the "Install without restart" button.

**Configure Dependency-Check Tool:**

- After installing the Dependency-Check plugin, you need to configure the tool.
- Go to "Dashboard" → "Manage Jenkins" → "Global Tool Configuration."
- Find the section for "OWASP Dependency-Check."
- Add the tool's name, e.g., "dc"
- Save your settings.



**Add DockerHub Credentials:**

- To securely handle DockerHub credentials in your Jenkins pipeline, follow these steps:
  - Go to "Dashboard" → "Manage Jenkins" → "Manage Credentials."
  - Click on "System" and then "Global credentials (unrestricted)."
  - Click on "Add Credentials" on the left side.
  - Choose "Secret text" as the kind of credentials.
  - Enter your DockerHub credentials (Username and Password) and give the credentials an ID (e.g., "docker").
  - Click "OK" to save your DockerHub credentials.

Now, you have installed the Dependency-Check plugin, configured the tool, and added Docker-related plugins along with your DockerHub credentials in Jenkins. You can now proceed with configuring your Jenkins pipeline to include these tools and credentials in your CI/CD process.

```groovy

@Library('Shared') _  // Referencing your shared library

pipeline {
    agent any
    environment {
        SONAR_HOME = tool "Sonar"
        DOCKER_IMAGE = "bankapp"
        GIT_REPO = "https://github.com/pundir8372/DevOps-mega-project.git"
        GIT_BRANCH = "DevSecOps"
    }
    stages {
        stage("Clean Workspace") {
            steps {
                cleanWs()
            }
        }
        stage("Code Clone") {
            steps {
                script {
                    code_checkout(GIT_REPO, GIT_BRANCH)
                }
            }
        }
        stage("SonarQube Quality Analysis") {
            steps {
                sonarqube_analysis('Sonar', 'bankapp', 'bankapp')
            }
        }
        stage("OWASP Dependency Check") {
            steps {
                owasp_dependency()
            }
        }
        stage("Sonar Quality Gate Scan") {
            steps {
                sonarqube_code_quality()
            }
        }
        stage("Trivy File System Scan") {
            steps {
                trivy_scan()
            }
        }
        stage("Docker Build") {
            steps {
                docker_build('bankapp', 'latest', 'dockerHubUser')
            }
        }
        stage("Push to Docker Hub") {
            steps {
                docker_push('bankapp', 'latest', 'pundirsahil')
            }
        }
    }
    post {
        success {
            echo "Pipeline completed successfully!"
            emailext (
                subject: "SUCCESS: Jenkins Pipeline for ${DOCKER_IMAGE}",
                body: """
                    <div style="font-family: Arial, sans-serif; padding: 20px; border: 2px solid #4CAF50; border-radius: 10px;">
                        <h2 style="color: #4CAF50;">🎉 Pipeline Execution: SUCCESS 🎉</h2>
                        <p style="font-size: 16px; color: #333;">
                            Hello Team,
                        </p>
                        <p style="font-size: 16px; color: #333;">
                            The Jenkins pipeline for <strong style="color: #4CAF50;">${DOCKER_IMAGE}</strong> completed <strong style="color: #4CAF50;">successfully</strong>!
                        </p>
                        <table style="width: 100%; border-collapse: collapse; margin-top: 20px;">
                            <tr style="background-color: #f2f2f2;">
                                <th style="text-align: left; padding: 8px; border: 1px solid #ddd;">Details</th>
                                <th style="text-align: left; padding: 8px; border: 1px solid #ddd;">Values</th>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Git Repository</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${GIT_REPO}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Branch</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${GIT_BRANCH}</td>
                            </tr>
                        </table>
                        <p style="font-size: 16px; color: #333; margin-top: 20px;">
                            Visit <a href="${BUILD_URL}" style="color: #4CAF50;">Pipeline Logs</a> for more details.
                        </p>
                        <p style="font-size: 16px; color: #333; margin-top: 20px;">
                            Thanks,<br>
                            <strong>Jenkins</strong>
                        </p>
                    </div>
                """,
                to: "pundirsahil320@gmail.com",
                from: "jenkins@example.com",
                mimeType: 'text/html'
            )
        }
        failure {
            echo "Pipeline failed. Please check the logs."
            emailext (
                subject: "FAILURE: Jenkins Pipeline for ${DOCKER_IMAGE}",
                body: """
                    <div style="font-family: Arial, sans-serif; padding: 20px; border: 2px solid #F44336; border-radius: 10px;">
                        <h2 style="color: #F44336;">🚨 Pipeline Execution: FAILURE 🚨</h2>
                        <p style="font-size: 16px; color: #333;">
                            Hello Team,
                        </p>
                        <p style="font-size: 16px; color: #333;">
                            Unfortunately, the Jenkins pipeline for <strong style="color: #F44336;">${DOCKER_IMAGE}</strong> has <strong style="color: #F44336;">failed</strong>.
                        </p>
                        <table style="width: 100%; border-collapse: collapse; margin-top: 20px;">
                            <tr style="background-color: #f2f2f2;">
                                <th style="text-align: left; padding: 8px; border: 1px solid #ddd;">Details</th>
                                <th style="text-align: left; padding: 8px; border: 1px solid #ddd;">Values</th>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Git Repository</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${GIT_REPO}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Branch</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${GIT_BRANCH}</td>
                            </tr>
                        </table>
                        <p style="font-size: 16px; color: #333; margin-top: 20px;">
                            Visit <a href="${BUILD_URL}" style="color: #F44336;">Pipeline Logs</a> for more details.
                        </p>
                        <p style="font-size: 16px; color: #333; margin-top: 20px;">
                            Thanks,<br>
                            <strong>Jenkins</strong>
                        </p>
                    </div>
                """,
                to: "pundirsahil320@gmail.com",
                from: "jenkins@example.com",
                mimeType: 'text/html'
            )
        }
    }
}



If you get docker login failed errorr

sudo su
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins


```



**Phase 4: Cleanup**

 **Cleanup AWS EC2 Instances:**
    - Terminate AWS EC2 instances that are no longer needed.
