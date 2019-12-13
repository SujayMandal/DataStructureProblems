# Dynamic-Pricing Application

## Follow below steps to setup the project

### Code setup 
	* Clone repository to local system (If you do not have access, contact Team lead / Manager)
	  git clone ssh://git@bitbucket.altidev.net:7999/ra/dynamicpricing.git
			
### Workspace setup using Eclipse IDE
	* Click on File -> Import -> Existing Maven Projects
	* Navigate to cloned dynamicpricing directory.
	* Check all checkboxes.
	* Check & name the working set as dp-parent.
	* Click on finish, Code base is setup now. 
	* Create a new maven build configuration as Build-Configuration =>
			Base: dp-parent
			Goals : clean install
			check skip tests checkbox
			Add parameter bambooBuildNumber with value as 1
	* open dp-app pom.xml, comment "executions" tag from stary to end.
	* open dp-ui pom.xml, comment "executions" tag from stary to end.
	* run maven -> update project for dp-parent to download pom dependencies.
	* replace content of WebSecurityConfig.java with the file in dp-parent.
	* place DpCorsFilter.java file in com.fa.dp.security.filter package under dp-app, which is available in dp-parent
	* run Build-Configuration to build the project. 
	
### Database setup
	* create dynamicpricing database in local mysql db.
	* Run liquibase update 
			Base: dp-db 
			Goals: liquibase:update
			liquibase.changeLogFile : db-changelog-master.xml
	
### UI editor Visual Studio Code setup
	* open cloned dynamicpricing\dp-ui folder.
	* View -> Integrated Terminal
	* navigate to dp-ui location in terminal.
	* run "npm install" command.
	* uncomment "<base href" for Node and comment the same for Tomcat in index.html
	* uncomment "API_URL" for Node and comment the same for Tomcat in app-config.constants.ts
	* go to header.component.html write your network id after "{{username}}"
	* start node server running "ng serve -port=4300" command from integrated terminal.

###	Deploying application to external tomcat server
	* create tomcat-start-dp.bat and place it in the tomcat folder.
			set CATALINA_HOME="D:\apache-tomcat\apache-tomcat-8-DPA"
			set CATALINA_BASE="D:\apache-tomcat\apache-tomcat-8-DPA"
			set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_131\bin"
			set JPDA_ADDRESS=8000
			set JPDA_TRANSPORT=dt_socket
			set CATALINA_OPTS="-Dspring.config.location=D:\DPA-Workspace\localProperties\application.properties" "-Dhazelcast.config=D:\DPA-Workspace\localProperties\hazelcast.xml"
			"-Dlogging.config=D:\DPA-Workspace\localProperties\log4j2.xml"  
			%CATALINA_HOME%\bin\catalina.bat  jpda run
	* get the current application.properties, hazelcast.xml, log4j2.xml from dp-parent and place them appropriately as per above configuration.
	* change username, password for "#DYNAMIC PRICING code database settings" in application.properties and dp-
	* create following folder D:/Logs/dp for logs
			otherwise change log folder location in log4j2.xml and application.properties accordingly to new location.
	* copy the dp.war from dp-app -> target and place it in external-tomcat\webapps folder.
	* set Connector port="9084"  for protocol="HTTP/1.1" in server.xml of external-tomcat\conf
	* run tomcat-start-dp.bat from cmd terminal.