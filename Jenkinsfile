pipeline {
    agent any

    options {
	skipStagesAfterUnstable()
    }

    environment {
    	SERVER = "pcalex:8080"
    }

    stages {
        stage('build') {
            steps {
                sh './gradlew clean build buildDeb -PbuildNumber=$BUILD_NUMBER'
            }
	    post {
    	     	 always {
		     archiveArtifacts artifacts: 'build/distributions/*', fingerprint: true
	 	     junit 'build/test-results/**/*.xml'
	         }
            }
        }
	stage('Acceptance') {
	    steps {
	    	  sh 'rsync -avh --delete . pcalex:~/price/'
		  sh "ssh pcalex 'cd ~/price ; ./scripts/prepare_env 8080 price_stage'"
	          sh "ssh pcalex 'cd ~/price ; ./scripts/provision_lxc build/distributions/*.deb price_stage'"
		  sh './scripts/smoketest pcalex:8080'

		  sh './gradlew integrationTest'
	    }
	    post {
    	     	 always {
		 	junit 'build/test-results/**/*.xml'
	         }
            }
	}
	stage('Deploy') {
            steps {
	        echo "deploy"
                  sh 'rsync -avh --delete . pcalex:~/price/'
		  sh "ssh pcalex 'cd ~/price ; ./scripts/prepare_env 7070 price_prod'"
	          sh "ssh pcalex 'cd ~/price ; ./scripts/provision_lxc build/distributions/*.deb price_prod'"
		  sh './scripts/smoketest pcalex:7070'
            }
        }
    }
   
}