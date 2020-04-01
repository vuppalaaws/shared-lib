String call() {
 dir ('POCConsoleApp/')
        {
       bat '''"C:/Program Files (x86)/Microsoft Visual Studio/2017/Community/MSBuild/15.0/Bin/MSBuild.exe" /t:build /p:configuration=Release /p:Platform="Any CPU" '''
	   }
    bat '''
	set FormatedDate=%DATE:~10,4%%DATE:~4,2%%DATE:~7,2%
    set hr=%time:~0,2%
	set hr=%hr: =0%
	set mn=%time:~3,2%
	set sec=%time:~6,2%
    set filename=%FormatedDate%-%hr%%mn%%sec%
    echo %filename% > test
	zip -r POCConsoleApp_%FormatedDate%-%hr%%mn%%sec%.zip POCConsoleApp/POCConsoleApp/bin           
          '''
	stage('Publish the artifacts from artifactory'){  
		def artifactoryServer = ssgaArtifactoryServer()
		def uploadSpec = """{
			"files": [
				{
				  "pattern": "POCConsoleApp_.*.zip",
				  "target": "nuget-local/SSGA/POCConsoleAPP/",
				   "regexp": "true"
				}
			]
		}""" 
      def buildInfo = artifactoryServer.upload(spec: uploadSpec)
	  artifactoryServer.publishBuildInfo(buildInfo)
}

this
