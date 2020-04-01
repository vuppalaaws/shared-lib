
def setVersion(versionToSet) {
	setVersion(false, versionToSet)
}

def setVersion(debug, versionToSet) {
	def optionalDebugFlag = debug ? '-X ' : ''
	Objects.requireNonNull(versionToSet, 'versionToSet must not be null')
	String versionToUse = versionToSet.toString().trim()
	//checkForCorrectVersionFormat(versionToUse)
	def options = "${optionalDebugFlag}versions:set -DgenerateBackupPoms=false -DnewVersion=${versionToUse}"
	executeMVN(options, false, false)
	echo "Changed POM version to ${getVersion()}"
}

String getVersion() {
	def pom = readMavenPom(file: 'pom.xml')
	def groupId = (pom.groupId ?: pom.parent.groupId).toString()
	def artifactId = pom.artifactId
	def version = (pom.version ?: pom.parent.version).toString()
	echo "${groupId}:${artifactId}:${version}"
	return version
}

String executeMVN(options, returnStdout, updateSnapshots) {
	if (options == null || options.toString().trim().isEmpty()) {
		throw new IllegalArgumentException('options must not be null or empty')
	}
	def optionsWithoutTabs = options.toString().trim().replace('\t', ' ')
	def optionsPotentiallyWithUpdateSnapshots = !updateSnapshots || optionsWithoutTabs.contains('-U') || optionsWithoutTabs.contains('--update-snapshots') ? optionsWithoutTabs : "--update-snapshots ${optionsWithoutTabs}"
	def defaultSettingsFile = '/data/devops/maven/settings.xml'
	def optionsToUse = optionsPotentiallyWithUpdateSnapshots.contains('-s ') || optionsPotentiallyWithUpdateSnapshots.contains('--settings ') ? optionsPotentiallyWithUpdateSnapshots : "--settings ${defaultSettingsFile} ${optionsPotentiallyWithUpdateSnapshots}"
	def regexToMatch = '[- _/:,\\.=+[a-zA-Z0-9]]+'
	if (!optionsToUse.matches(regexToMatch)) {
		throw new IllegalArgumentException("options must match the regular expression ${regexToMatch}: ${options}")
	}
	// Get the maven install directory as defined in Jenkins
	def mvnHome = tool 'maven'
	sh returnStdout: returnStdout, script: "${mvnHome}/bin/mvn ${optionsToUse}"
}

