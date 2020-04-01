def call(buildType, filename, artifactoryRepoName, repoPath) {
	switch(buildType) { 
    //If the buildtype of the project is .NET, the pipeline will get the build information from the Jenkins.properties file.
    case '.NET':
		def props = readProperties file:'Jenkins.properties' 
    	env.PR_PRODUCT_NUMBER = props['BUILD_VERSION']
        env.PR_PROJECT_NAME = props['PROJECT_NAME']
        env.PR_PROJECT_KEY = props['PROJECT_KEY']
    	currentBuild.displayName = env.PR_PRODUCT_NUMBER  + "." + env.BUILD_NUMBER
    break

    //If the buildtype of the project is Maven, the pipeline will get the build information from the pom.xml file
    case 'Maven':
		def pom = readMavenPom file:'pom.xml'
    	env.PR_PRODUCT_NUMBER = pom.version
        env.PR_PROJECT_NAME = pom.properties.projectName
        env.PR_PROJECT_KEY = pom.properties.projectKey
		env.repoArtifact = "*.jar"
		env.repoTarget = "NA"
    	currentBuild.displayName = env.PR_PRODUCT_NUMBER  + "." + env.BUILD_NUMBER
        mavenUtils.setVersion(currentBuild.displayName)
    break

	case 'Batch':
		def ts = PRGetTimestamp()
		env.repoArtifact = "${filename}-${ts}.zip"
        env.mavenGoals = "NA" 
		env.repoTarget = "${artifactoryRepoName}/${repoPath}"
		print "repoTarget: ${env.repoTarget}, repoArtifact: ${env.repoArtifact}"
    break

    //Print out message if an invalid build type was not provided.
    default:
		//throw new IllegalArgumentException("Invalid build type: (${buildType}")
		println("Invalid build type: ${buildType} received")
   }
 }
