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
	          sh './scripts/provision_lxc build/distributions/*.deb stage'
		  sh './scripts/smoketest 192.168.45.45'
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