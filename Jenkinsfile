pipeline {
    agent any

    options {
	skipStagesAfterUnstable()
    }


    stages {
        stage('build') {
            steps {
                sh './gradlew clean build buildDeb -DbuildNumber=$BUILD_NUMBER'
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
	          sh "ssh pcalex 'cd ~/price ; ./scripts/provision_lxc build/distributions/*.deb stage 8080'"
		  sh './scripts/smoketest pcalex:8080'
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