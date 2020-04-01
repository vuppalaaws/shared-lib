String call(){
	//def ts = ssgaGetTimestamp()
    //def zipFileName = "${pipelineParams.filename}-${ts}.zip"
	dir('') {
		try{
			sh 'rm *.zip'
		}catch ( err){
			echo "Failed to remove ZIP file, file doesn't exist : ${err}"
		}
	}
    dir('') {
        zip archive: true, glob: '', dir: '', zipFile: "${env.repoArtifact}"
		sh '''ls -lrt '''
	}
}

this