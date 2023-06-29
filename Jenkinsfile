pipeline {
    agent any
    options {
        timeout(time: 1, unit: 'HOURS') // set timeout 1 hour
    }
    environment {
        TIME_ZONE = 'Asia/Seoul'
        PROFILE = 'local'

        REPOSITORY_CREDENTIAL_ID = credentials('GitCredential')
        REPOSITORY_URL = credentials('RepositoryUrl')
        TARGET_BRANCH = 'master'

        AWS_CREDENTIAL_NAME = credentials('AWSCredentials')
        ECR_PATH = credentials('ecrPath')
        IMAGE_NAME = 'user-service'
        REGION = credentials('region')
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
                script{
                    sh '''
                    #!/bin.bash
                    cat>Dockerfile<<-EOF
                    FROM openjdk:11
                    WORKDIR /app
                    RUN sudo cat /tmp/application.yml > /app/src/main/resources/application.yml
                    CMD ["./gradlew", "clean", "build"]
                    COPY /build/libs/user-service-1.0.jar userSVC.jar
                    CMD ["java", "-jar", "userSVC.jar"]
                    EOF'''
                }
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
                    // cleanup current user docker credentials                   sh 'rm -f ~/.dockercfg ~/.docker/config.json || true'

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
