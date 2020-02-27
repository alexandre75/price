pipeline {
    agent any

    options {
	skipStagesAfterUnstable()
    }


    stages {
        stage('build') {
            steps {
                sh './gradlew build'
            }
	    post {
    	     	 always {
		     archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
	 	     junit 'build/reports/**/*.xml'
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