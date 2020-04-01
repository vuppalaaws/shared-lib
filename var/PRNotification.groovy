//@NonCPS
def renderTemplate(input, binding) {
    def engine = new groovy.text.GStringTemplateEngine()
    def template = engine.createTemplate(input).make(binding)
    return template.toString()
}

def call(emailNotification, recieverEmail) {	
	if(emailNotification=='true'){
		//Code to find author_name
		def committerEmail = sh (
								script: 'git --no-pager show -s --format=\'%ae\'',
								returnStdout: true
								).trim()
		def rawBody = libraryResource 'com/ssga/emailtemplate/JenkinsEmailTemplate.html'       
		def binding = [
            applicationName : env.JOB_NAME,
            sourceBranch    : env.GIT_BRANCH,
            buildNumber     : currentBuild.displayName,
            developer       : "${committerEmail}",
            buildUrl        : env.BUILD_URL,
            buildResult     : currentBuild.currentResult
		]

		def render = renderTemplate(rawBody,binding)
	   	def subjectLine = env.JOB_NAME + ' - ' + currentBuild.displayName + ' - ' + currentBuild.currentResult
   
		def jobName = currentBuild.fullDisplayName
        emailext body: "${render}",
        mimeType: 'text/html',
		from: 'jenkins-dev@ssga.com',
        subject: "${subjectLine}",
        to: "${recieverEmail}",
        replyTo: "${recieverEmail}",
        recipientProviders: [[$class: 'CulpritsRecipientProvider']]
		}
}

