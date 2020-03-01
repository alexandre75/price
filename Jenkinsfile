pipeline {
    agent any

    options {
	skipStagesAfterUnstable()
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
		  sh "ssh pcalex 'cd ~/price ; ./scripts/prepare_env 8080'"
	          sh "ssh pcalex 'cd ~/price ; ./scripts/provision_lxc build/distributions/*.deb stage'"
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
                //sh './deploy production'
            }
        }
    }
   
}