String call(mavenGoals, buildType) {
	switch(buildType){
		case 'Maven': 
			ssgaMavenBuild(mavenGoals)
			break
		case '.NET':
			ssgaDotNetBuild()
			println("This is for .NET build")
			break
		case 'Batch' :
			ssgaBatchBuild()
			break
		default:
			println("No build type provided")
			break
	}						
}

this