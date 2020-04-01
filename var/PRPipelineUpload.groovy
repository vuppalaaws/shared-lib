def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
	
	
    pipeline{
    
        agent {label "${pipelineParams.agentLabel}"}

        stages{
                    

            stage("Build"){
                when{ 
                    expression{ return "Batch".equals(pipelineParams.buildType)};
                }
				steps{
					//PRApplicationBuild(pipelineParams.mavenGoals, pipelineParams.buildType)
					script {
					def ts = PRGetTimestamp()
                    def zipFileName = "${pipelineParams.filename}-${ts}.zip"
				   dir('') {
						try{
						 sh 'rm *.zip'
						}catch ( err){

							 echo "Failed to remove ZIP file, file doesn't exist : ${err}"
						}
					}
        
					dir('') {
                     zip archive: true, glob: '', dir: '', zipFile: "${zipFileName}"

					sh '''ls -lrt '''
					}
					
					}
				}
            }

			                    
		  
		   stage('Publish to artifactory') {
		      steps{

		      script {

		                    def ts = PRGetTimestamp()
				    def zipFileName = "${pipelineParams.filename}-${ts}.zip"
		       
				def artifactoryServer = PRArtifactoryServer()
				def uploadSpec = """{
					"files": [{
						"pattern": "${zipFileName}",
						"target": "${pipelineParams.reponame}/${pipelineParams.repopath}/$zipFileName",
						"recursive": "true",
						"regexp": "true"
					}]
				}"""
				def buildInfo = artifactoryServer.upload(spec: uploadSpec)
				artifactoryServer.publishBuildInfo(buildInfo)
			}
			}	
				
		}
	  
          
          	//stage('Publish'){
                //when{
                	//branch 'develop' //'feature/bgioggia'
                //}
            	//steps{
                  	//PRDeployBuild()
                  ///echo "publish"
               // }
            //}
          
          

		}
        post{
				always{
					 //PRNotification(pipelineParams.emailNotification, pipelineParams.recieverEmail)
					 echo "Notification sent"
				}
        }     
    }//End of Pipeline 
}
