pipeline {
    agent any

    options {
	skipStagesAfterUnstable()
    }


    stages {
        stage('build') {
            steps {
                sh './gradlew clean build buildDeb'
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
	    	  echo "not implemented"
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