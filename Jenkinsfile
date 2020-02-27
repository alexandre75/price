pipeline {
    agent any

    options {
	skipStagesAfterUnstable()
    }


    stages {
        stage('build') {
	    tools {
	    	  gradle "gradle621"
	    }
            steps {
                sh './gradlew build'
            }
	    post {
    	     	 always {
		     archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
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