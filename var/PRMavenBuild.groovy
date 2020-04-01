String call(mavenGoals) { 
			//def server = Artifactory.server('artifactory_master1')
			def server = Artifactory.server 'artifactory_master1'
			def rtMaven = Artifactory.newMavenBuild()
			//Below condition is to run the build only for develop, release and hostfix branches
  			/*if(env.GIT_BRANCH=="feature/bgioggia"){    //(env.GIT_BRANCH=="develop" || env.GIT_BRANCH.contains("release") ||env.GIT_BRANCH.contains("hostfix")){
			rtMaven.deployer server: server, releaseRepo: 'maven-local', snapshotRepo: 'maven-local'
			rtMaven.deployer.deployArtifacts = true
			}*/
			rtMaven.deployer.deployArtifacts = true
			rtMaven.tool = 'maven'	
			//def buildInfo = rtMaven.run pom: './pom.xml', goals: mavenGoals		
			rtMaven.run pom: './pom.xml', goals: mavenGoals
}

this
