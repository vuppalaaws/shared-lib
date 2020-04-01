/*
    Description: This module is designed to manage all deployments to the Artifactory system (maven, nuget,pypi,etc).
    The primary information needed for a successeful deployment to artifactory is the pattern and target  for the application
    Example:
    "files": [
        {"pattern": "<<ZipFileName>>}",
        "target": "nuget-local/<<RepositoryPath>>",
        "recursive": "false",
        "regexp": "true"},
        {"pattern": "<<ZipFileName>>",
        "target": "nuget-local/<<RepositoryPath>>",
        "recursive": "false",
        "regexp": "true"}
    ]

    The module map contains the following:
    repoType:           Indicates the repository object type (maven, pypi, nuget,generic).
    repoPrivateName:    This parameter is only applicable when the repoType is "generic"
                        This parameter indicates the name of the private repository where the artifact should be uploaded.
    repoPattern:        Indicates the name of the file to be deployed to artifactory.
    repoTarget:         Indicates the repository path where the artifact should be deployed.
                        The repoTarget should not contain the repository name (i.e. maven-local, nuget-local, etc)    
    repoArtifact:       The name of the artifact which should be deployed to Artifactory.
*/

def call (repoType, repoArtifact, artifactoryRepoName, repoPath)
{
    def strPattern
    def strTarget
    //def repoName = "sharedlib-generic"
	
	if ("${env.repoTarget}" == null || "${env.repoTarget}" == "NA")
		repoTarget = "${artifactoryRepoName}/${repoType}/${currentBuild.displayName}"

    sh '''ls -lrt'''

    switch("${repoType}"){
        case "maven":
    	    repoPath = "${repoType}"
		case "nuget":
			repoPath = "${repoType}"
		case "pypi":
			repoPath = "${repoType}"
		case "generic":
			strPattern = "${repoArtifact}"
			//strTarget = "${repoPrivateName}"
			repoPath = "${repoType}"
		default:
			throw new IllegalArgumentException("Invalid Repository Type: ${repoType}")
		}
    																									    			
    def artifactoryServer = ssgaArtifactoryServer()
    def uploadSpec = 	"""{
							"files": [{
							"pattern": "${repoArtifact}",
							"target": "${repoTarget}/",
							"recursive": "true"	
							}]	
						}"""

    def buildInfo = artifactoryServer.upload(spec: uploadSpec)
    artifactoryServer.publishBuildInfo(buildInfo)
}