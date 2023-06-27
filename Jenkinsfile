pipeline {
    agent any
    options {
        timeout(time: 1, unit: 'HOURS') // set timeout 1 hour
    }
    environment {
        TIME_ZONE = 'Asia/Seoul'
        PROFILE = 'local'

        REPOSITORY_CREDENTIAL_ID = credentials('GitCredential')
        REPOSITORY_URL = 'https://github.com/5-A-M/Pilivery-User-Service.git'
        TARGET_BRANCH = 'master'

        AWS_CREDENTIAL_NAME = credentials('AWSCredentials')
        ECR_PATH = '806308213817.dkr.ecr.ap-northeast-2.amazonaws.com'
        IMAGE_NAME = 'user-service'
        REGION = 'ap-northeast-2'
    }
    stages{
        stage('init') {
            steps {
                echo 'init stage'
                deleteDir()
            }
            post {
                success {
                    echo 'success init in pipeline'
                }
                failure {
                    error 'fail init in pipeline'
                }
            }
        }
        stage('clone project') {
            steps {
                git url: "$REPOSITORY_URL",
                    branch: "$TARGET_BRANCH",
                    credentialsId: "$REPOSITORY_CREDENTIAL_ID"
                sh "ls -al"
            }
            post {
                success {
                    echo 'success clone project'
                }
                failure {
                    error 'fail clone project' // exit pipeline
                }
            }
        }
        stage('build project') {
            steps {
                sh '''
        		 echo "Fake Build Start."
        		 '''
            }
            post {
                success {
                    echo 'success build project'
                }
                failure {
                    error 'fail build project' // exit pipeline
                }
            }
        }

        stage('docker build and push to ecr') {
            steps {
                script{
                    // cleanup current user docker credentials
                    sh 'rm -f ~/.dockercfg ~/.docker/config.json || true'

                    echo "Success Delete Docker Config"


                    docker.withRegistry("https://${ECR_PATH}", "ecr:${REGION}:AWSCredentials") {
                      def image = docker.build("${ECR_PATH}/${IMAGE_NAME}:${env.BUILD_NUMBER}")
                      image.push()
                    }

                }
            }
            post {
                success {
                    echo 'success upload image'
                }
                failure {
                    error 'fail upload image' // exit pipeline
                }
            }
        }
    }
}