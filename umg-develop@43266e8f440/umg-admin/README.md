# umg-admin 

## Setup umg-admin module

    1. Hazelcast mode setup
        a.  
        umg-admin can be setup as eiher hazelcast-client mode or as hazelcast member mode. 
       Default/production setup is hazelcast client mode.  
    
    2. web-ui module is to be used as deployable artifact for umg-admin.
      
    
    3. Deploying the module into external tomcat.



-Ddb.properties=D:\conf\umg-admin\db.properties 
-Dlog4j.configurationFile=file:\\D:\conf\umg-admin\log4j2.xml 
-Dhazelcast.config=D:\conf\umg-admin\hazelcast-config-member.xml 
-DhttpConnectionPooling.properties=file:\D:\conf\umg-admin\httpConnectionPooling.properties 
-DapplicationSecurity.config=D:\conf\umg-admin\applicationContext-security.xml
-DapplicationContextSecurity.properties=D:\conf\umg-admin\applicationContextSecurity.properties
-Dumg.properties=D:\conf\umg-admin\umg.properties -Dsanpath=D:\conf\


    4. In case we want to run umg-admin using smart tomcat extension, make sure deployment path, vm options and context is set as follows:
       Deployment=<<Path to umg>>\umg-admin\sdc\web-ui\src\main\webapp
       Context=/web-ui
       VM Options= 
       
-Ddb.properties="D:\RA_Project_Stuff\config\umg-admin\db.properties" 
-Dlog4j.configurationFile="file:\\D:\RA_Project_Stuff\config\umg-admin\log4j2.xml"
-Dhazelcast.config="D:\RA_Project_Stuff\config\umg-admin\hazelcast-config.xml" 
-DhttpConnectionPooling.properties="file:\D:\RA_Project_Stuff\config\umg-admin\httpConnectionPooling.properties" 
-DapplicationSecurity.config="D:\RA_Project_Stuff\config\umg-admin\applicationContext-security.xml" 
-DapplicationContextSecurity.properties="D:\RA_Project_Stuff\config\umg-admin\applicationContextSecurity.properties" 
-Dumg.properties="D:\RA_Project_Stuff\config\umg-admin\umg.properties" 
-Dsanpath="D:\RA_Project_Stuff\sanpath\san\"
        
       Note: Update file paths specified in vm options accordingly.
       
       Refer to screenshot availabe under  setup/smart tomcat config/smart_tomcat_config.zip -> umg-admin-smart_tomcat_config.PNG
       
