@Library('Shared')_

pipeline{
    agent any
    
    parameters{
        string(name: 'IMAGE_VERSION',defaultValue: 'latest', description: "Image tag")
    }
    
    stages{
        stage("Code"){
            steps{
                clone("https://github.com/Amitabh-DevOps/DevOps-mega-project.git","project")
                echo "Code clonning done."
            }
        }
        stage("Build"){                                                             
            steps{
                dockerbuild("mega-project","${params.IMAGE_VERSION}")
                echo "Code build bhi hogaya."
            }
        }
        stage("Push to DockerHub"){
            steps{
                dockerpush("dockerHub","mega-project","${params.IMAGE_VERSION}")
                echo "Push to dockerHub is also done."
            }
        }
    }
}
