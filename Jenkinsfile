pipeline {
    agent any
    options {
        timeout(time: 1, unit: 'HOURS') // set timeout 1 hour
    }
    environment {
        TIME_ZONE = 'Asia/Seoul'
        PROFILE = 'local'

        REPOSITORY_CREDENTIAL_ID = credentials('GitCredential')
        REPOSITORY_URL = credentials('UserServiceRepositoryUrl')
        TARGET_BRANCH = 'master'

        HELM_REPOSITORY_URL = credentials('HELM_REPOSITORY_URL')
        HELM_TARGET_BRANCH = credentials('HELM_TARGET_BRANCH')

        AWS_CREDENTIAL_NAME = credentials('AWSCredentials')
        ECR_PATH = credentials('ecrPath')
        IMAGE_NAME = 'user-service'
        REGION = credentials('region')
    }
    stages{
        stage('Clean Workspace') {
            steps {
                echo '*********Clean Workspace*********'
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
        stage('Git Clone Application Code') {
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

        stage('Clone Application Secret') {
            steps {
                withCredentials([file(credentialsId: 'pilivery-backend-application-yml', variable: 'secretFile')]) {
                    sh "pwd & mkdir /var/lib/jenkins/workspace/${IMAGE_NAME}/src/main/resources"
                    dir('./src/main/resources') {
                        sh "cp ${secretFile} /var/lib/jenkins/workspace/${IMAGE_NAME}/src/main/resources/application.yml"
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                sh '''
        		 ./gradlew clean build 
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

        stage('Docker Build And Push To ECR') {
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

        stage("Clean Previous Workspace") {
            steps {
                echo '*********Clean Workspace*********'
                deleteDir()
            }

            post {
                success {
                    echo 'success clean workspace'
                }
                failure {
                    error 'fail clean workspace' // exit pipeline
                }
            }
        }

        stage('Git Clone Helm Chart Repository') {
            steps {
                git url: "$HELM_REPOSITORY_URL",
                    branch: "$HELM_TARGET_BRANCH",
                    credentialsId: "$REPOSITORY_CREDENTIAL_ID"
                sh "ls -al"
            }
        }

        stage('Update Helm Chart And PUsh') {
            steps {
                // Helm 차트의 이미지 버전 정보 변경
                // sh "sed -i 's/tag: .*/tag: v${env.BUILD_NUMBER}/' values.yaml"
                sh "ls -al"
                // 변경된 Helm 차트 파일 커밋
                // gitCommit('Update Helm chart')
            }

            post {
                always {
                    // 변경된 Helm 차트 파일 커밋 푸쉬
                    // gitPush()
                    echo "Helm Chart Update"
                }

                success {
                    echo 'success build project'
                }
                failure {
                    error 'fail build project' // exit pipeline
                }
            }
        }
    }
}

def gitCommit(message) {
  sh "git config user.email '5am-production@naver.com'"
  sh "git config user.name '5am-production'"
  sh "git add values.yaml"
  sh "git commit -m '${message}'"
}

def gitPush() {
  sh "git push origin master"
}
