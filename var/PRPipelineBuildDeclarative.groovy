def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
    pipeline{    
        agent {label "${pipelineParams.agentLabel}"}
        stages{            
			stage("Prepare Build Environment"){
				steps
				{
					PRPrepareEnvironment(pipelineParams.buildType, pipelineParams.filename, pipelineParams.artifactoryRepoName, pipelineParams.repopath)
					//println("Branch: ${branch}")
				}
			}
			stage("Build Application"){
				//when{ 
				//    expression{ return "Maven".equals(pipelineParams.buildType)};
				//}
				steps{
					PRApplicationBuild(pipelineParams.mavenGoals, pipelineParams.buildType)
				}
			}          
			stage('SonarQube Scan'){
				//println("Branch: ${branch}")
				when{
					allOf{ 
						//branch 'develop' //'feature/bgioggia'
						expression{return  pipelineParams.sonarQubeScan !=null && 'true' == pipelineParams.sonarQubeScan}
					} 
				}
				steps{ 
					PRSonarAnalysis(pipelineParams.buildType)
				}
			}
			stage('Publish'){
				steps{
					script{
						if (pipelineParams.artifactoryRepoName == null)
							pipelineParams.artifactoryRepoName = "sharedlib-generic"
					}
					PRDeployToArtifactory(pipelineParams.repoType, env.repoArtifact, pipelineParams.artifactoryRepoName, pipelineParams.repoPath)
				}
			}
        }
        post{
			always{
				//PRNotification(pipelineParams.emailNotification, pipelineParams.recieverEmail)
                echo "hello"
			}
        }     
    }//End of Pipeline 
}
