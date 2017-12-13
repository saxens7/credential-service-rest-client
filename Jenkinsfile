UPSTREAM_TRIGGERS = [
  // List of upstream repositories 
    "<repo1>",
    "<repo2>",
    "<repo3>"
]
properties(getBuildProperties(upstreamRepos: UPSTREAM_TRIGGERS))

pipeline {    
    parameters {
        choice(choices: 'OFF\nON', description: 'Please select appropriate flag (master and stable branches will always be ON)', name: 'Deploy_Stage')
    }
    agent {
        node {
            label 'maven-builder'
            customWorkspace "workspace/${env.JOB_NAME}"
        }
    }
    environment {
        GITHUB_TOKEN = credentials('github-02')
    }
    options { 
        skipDefaultCheckout()
        timestamps()
    }
    tools {
        maven 'linux-maven-3.3.9'
        jdk 'linux-jdk1.8.0_102'
    }
    stages {
        stage('Checkout') {
            steps {
                doCheckout()
            }
        }
        stage('TravisCI Linter') {
            steps {
                doTravisLint()
            }
        }
        stage('Build') {
            steps {
                script {
                    sh "mvn clean install -Dmaven.repo.local=.repo"
                }
            }
        }
        stage('PasswordScan') {
            steps {
                doPwScan()
            }
        }
        stage('Upload to Repo') {
            steps {
                uploadArtifactsToArtifactory()
            }
        }
        stage('Record Test Results') {
            steps {
                junit '**/target/*-reports/*.xml'
            }
        }
        stage('Deploy') {
            steps {
                doMvnDeploy()
            }
        }
        stage('SonarQube Analysis') {
            steps {
                doSonarAnalysis()    
            }
        }
        stage('Third Party Audit') {
            steps {
                doThirdPartyAudit()
            }
        }
        stage('Github Release') {
            steps {
                githubRelease()
            }
        }
        stage('NexB Scan') {
            steps {
                doNexbScanning()
            }
        }
        stage('Run pytest Scanner') {
            steps {
                runPyTestScanner()
            }
    	}
    }
    post {
        always {
            cleanWorkspace()   
        }
        success {
            successEmail()
        }
        failure {
            failureEmail()
        }
    }
}
