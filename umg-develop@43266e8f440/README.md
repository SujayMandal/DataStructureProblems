#  REALAnalytics

## Setup workspace

	1. Checkout REALAnalytics code base fom following repository.
		git clone ssh://git@bitbucket.altidev.net:7999/ra/umg.git 
						OR
		https://atlas.altidev.net/stash/scm/ra/umg.git
	
	2. Import project to IDE as Existing maven project.
	   a. Copy the directories available under "setup/additional dependencies/dependencies.zip" to respective paths, specified as follows:
	      1. "7.16" to <<Path to m2>>\.m2\repository\com\altisource\lrm\matlab\javabuilder
	      2. "0.9.5" to <<Path to m2>>\.m2\repository\org\rosuda\JRI\JRI
	      3. "rf-iam"  to <<Path to m2>>\.m2\repository\com
	   b. Build the project using maven command
			..\umg> mvn -U clean install -DskipTests -DbambooBuildNumber=1
	
	3. Database setup
		Refer umg-db\README.md file for detailed steps to setup the database.
	
	4. umg-runtime setup
		Refer umg-runtime\README.md file for detailed steps to setup the umg-runtime module.

	5. umg-admin setup
       	Refer umg-admin\README.md file for detailed steps to setup the umg-admin module.

	6. modelet setup
		Refer modelet\README.md file for detailed steps to setup the umg-runtime module.