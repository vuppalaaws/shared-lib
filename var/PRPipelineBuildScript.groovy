Map call() {
	call([:])
}
Map call(map) {
  //Sonar flag which will define whether to execute Sonar Analysis or not
    def sonarAnalysis = true
  //Email notification flag and whom to send an email
    def emailNotification = true
	def recieverEmail = 'vuppala.aws@gmail.com'
  //Maven goals for building the project
    def mavenGoals = 'clean install'
  //Artifactory flag which will define whether to push to artifactory or not
    def pushToArtifactory = true
  //Build parameter for defining what type of build to execute
    def buildType = 'Maven'
  //Build parameter for defining what type of build to execute
    def executionNode = 'node1'
	
  //Below code is to get the parameters from Jenkins File and set it to Local Variables	
    def mapKeys = new ArrayList(map.keySet())
	for (int i = 0; i < mapKeys.size(); ++i) {
		def key = mapKeys[i]
		def value = map[key]
		switch(key) {
			case 'sonarAnalysis':
				sonarAnalysis = value ? true : false
				break
			case 'emailNotification':
				emailNotification = value ? true : false
				break
			case 'recieverEmail':
				//If email notification is true then we need to validate reciever email 
				if(emailNotification){
					if (null != value && !value.trim().isEmpty() && !value.equals('')) {
						recieverEmail = value
						break
					}
				}
			case 'mavenGoals':
				if(null != value && !value.trim().isEmpty() && !value.equals('')){
					mavenGoals = value
					break		
				}
			case 'pushToArtifactory':
				pushToArtifactory = value ? true : false
				break
			case 'buildType':
				if(null != value && !value.trim().isEmpty() && !value.equals('')){
					buildType = value
					break		
				}
			case 'executionNode':
				if(null != value && !value.trim().isEmpty() && !value.equals('')){
					executionNode = value
					break		
				}
			default:
				println("No changes to the default parameters")
		}
	}
  
    node(executionNode) {
		currentBuild.result = "SUCCESS"
		try
		{
			checkout scm					
			//This stage will build the source code and push to artifactory
			stage('Build') 
			{
				if(buildType=='Maven'){
					PRMavenUpdated(mavenGoals)
				}else if(buildType=='.NET'){
					//Add .NET build details	
				}				
			}
			//This stage will analyse the quality of the source code using sonar scanner
			stage('SonarQube analysis') 
			{
				if(sonarAnalysis){
					PRSonarUpdated()
				}
			}
		}
		catch (err) 
			{
				currentBuild.result = "FAILURE" 
				throw err
			}
		finally{
		  if(emailNotification){
				mail body: "Build status : ${currentBuild.result}\nPlease chcek the details in ${env.BUILD_URL}",
				from: 'jenkins-dev-Localhost:8080',
				subject: "Jenkins Job ${env.JOB_NAME} Build number ${env.BUILD_NUMBER} status",
				to: recieverEmail
		  }
		}
    }
}
