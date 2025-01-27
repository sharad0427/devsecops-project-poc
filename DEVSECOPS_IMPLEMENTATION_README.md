# End-to-End DevSecOps Implementation

## **Phase 1: Initial Setup and Deployment**

### **Step 1: Launch EC2 (Ubuntu 22.04)**
- Launch an AWS EC2 instance (Ubuntu) with the following specifications:
  - Instance type: `t2.large`
  - Root volume: `29GB`
- Connect to the instance using SSH.

---

### **Step 2: Clone the Code**
- Update all the packages and clone the application's code repository onto the EC2 instance:
    ```bash
    sudo apt-get update
    git clone https://github.com/pundir8372/DevOps-mega-project.git
    ```

---

### **Step 3: Install Docker and Docker Compose**
- Install Docker:
    ```bash
    sudo apt-get update
    sudo apt-get install docker.io -y
    sudo usermod -aG docker $USER  # Replace $USER with your username
    newgrp docker
    sudo chmod 777 /var/run/docker.sock
    ```

- Install Docker Compose:
    ```bash
    sudo apt-get install docker-compose-v2
    ```

---

## **Phase 2: Security**

### **1. Install SonarQube and Trivy**
- Install **SonarQube** to analyze code for vulnerabilities:
    ```bash
    docker run -d --name sonar -p 9000:9000 sonarqube:lts-community
    ```
    - Access SonarQube at `http://<public-ip>:9000` (default credentials: admin/admin).

- Install **Trivy** for container vulnerability scanning:
    ```bash
    sudo apt-get install wget apt-transport-https gnupg lsb-release
    wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
    echo deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main | sudo tee -a /etc/apt/sources.list.d/trivy.list
    sudo apt-get update
    sudo apt-get install trivy
    ```
    - Scan a Docker image with Trivy:
      ```bash
      trivy image <image-id>
      ```

---

### **2. Configure SonarQube**
- Integrate SonarQube with your CI/CD pipeline by adding the server details and authentication token in Jenkins:
  1. Generate a SonarQube token from the SonarQube dashboard.
  2. Add the token to Jenkins:  
     Go to **Jenkins Dashboard → Manage Jenkins → Credentials → Add Secret Text**.

---

## **Phase 3: CI/CD Setup**

### **1. Install Jenkins**
- Install Jenkins for automation:
    ```bash
    sudo apt update
    sudo apt install fontconfig openjdk-17-jre -y
    wget -O /usr/share/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
    echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
    sudo apt-get update
    sudo apt-get install jenkins -y
    sudo systemctl start jenkins
    sudo systemctl enable jenkins
    ```
- Access Jenkins at `http://<public-ip>:8080`.

---

### **2. Install Necessary Plugins**
- Go to **Manage Jenkins → Plugins → Available Plugins** and install:
  - OWASP Dependency-Check
  - SonarQube Scanner
  - Docker
  - Pipeline Stage View

---

### **3. Add DockerHub Credentials**
- Add DockerHub credentials to Jenkins:
  1. Go to **Manage Jenkins → Manage Credentials → System → Global credentials (unrestricted).**
  2. Click **Add Credentials** and select **Secret text**.
  3. Enter your DockerHub credentials (username and password).

---

### **4. Set Up Sonar Scanner and Dependency Check**
- Configure tools in Jenkins:
  1. **Sonar Scanner**:  
     Go to **Manage Jenkins → Global Tool Configuration** and add Sonar Scanner.
  2. **OWASP Dependency-Check**:  
     Go to **Global Tool Configuration** and add Dependency-Check tool.

---

## **Phase 4: Automate with CI/CD Pipeline**
- Create a Jenkins pipeline for automation with the following stages:
  - Clean workspace.
  - Clone code from GitHub.
  - Analyze code with SonarQube.
  - Perform OWASP Dependency-Check.
  - Scan for vulnerabilities with Trivy.
  - Build and push Docker images to DockerHub.
  - Deploy the application.

---

### **5. Post-Build Actions**
- Configure Jenkins to send email notifications upon build success or failure using **Email Extension Plugin**.
  - Success notifications should include deployment details such as the Git repository, branch name, and Docker image.
  - Failure notifications should provide details to debug the issue.

---

## **Application Deployment**
- Deploy the application using Docker Compose:
    ```bash
    docker-compose up -d
    ```
- Verify the deployment by accessing the application's endpoint in a browser.

---

**Phase 4: Cleanup**

1. **Cleanup AWS EC2 Instances:**
    - Terminate AWS EC2 instances that are no longer needed.
      

## **Conclusion**
This project involves setting up a secure, automated CI/CD pipeline for deploying a Spring Boot application using Jenkins, SonarQube, Trivy, and Docker. By following the steps outlined above, you can ensure high code quality and secure deployments in your DevSecOps workflow.


