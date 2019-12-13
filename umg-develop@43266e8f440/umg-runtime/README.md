# umg-runtime 

## Setup umg-runtime module

    1. umg-runtime module is to be used as deployable artifact for umg-runtime.
      
    2. Deploying the module into external tomcat.

-Ddb.properties="D:\RA_Project_Stuff\config\umg-runtime\db.properties"
-Dlog4j.configurationFile="file:\\D:\RA_Project_Stuff\config\umg-runtime\log4j2.xml"
-Dhazelcast.config="D:\RA_Project_Stuff\config\umg-runtime\hazelcast-config.xml"
-DhttpConnectionPooling.properties="file:\D:\RA_Project_Stuff\config\umg-runtime\httpConnectionPooling.properties"
-Dsanpath="D:\RA_Project_Stuff\sanpath\san\"
         
          Note: Update file paths accordingly.

    3. In case we want to run umg-runtime using smart tomcat extension, make sure deployment path, vm options and context is set as follows:
       Deployment=<<Path to umg>>\umg-runtime\src\main\webapp
       Context=/umg-runtime
       VM Options= 
       
-XX:+UseConcMarkSweepGC
-XX:+UseParNewGC
-XX:CMSInitiatingOccupancyFraction=70
-XX:+UseCMSInitiatingOccupancyOnly
-Ddb.properties="D:\RA_Project_Stuff\config\umg-runtime\db.properties"
-Dlog4j.configurationFile="file:\\D:\RA_Project_Stuff\config\umg-runtime\log4j2.xml"
-Dhazelcast.config="D:\RA_Project_Stuff\config\umg-runtime\hazelcast-config.xml"
-DhttpConnectionPooling.properties="file:\D:\RA_Project_Stuff\config\umg-runtime\httpConnectionPooling.properties"
-Dsanpath="D:\RA_Project_Stuff\sanpath\san\"
        
       Note: Update file paths specified in vm options accordingly.
       
       Refer to screenshot availabe under  setup/smart tomcat config/smart_tomcat_config.zip -> umg-runtime_smart_tomcat_config.PNG
       
