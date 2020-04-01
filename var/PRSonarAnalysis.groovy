String call(buildType) {
	switch(buildType){
		case 'Maven': 
			def mvnHome = tool 'maven'
			withSonarQubeEnv('Sonar') {
				// requires SonarQube Scanner for Maven 3.2+
				sh "'${mvnHome}/bin/mvn' org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.projectVersion=${currentBuild.displayName} -Dsonar.projectName=${env.PR_PROJECT_NAME} -Dsonar.projectKey=${env.PR_PROJECT_KEY}"
				}
			break
		case '.NET':
			println("This is for .NET Sonar build")
			break
		default:
			println("No build type provided")
			break
	}				
}

this
