Deployment scripts for each environment in AWS cloud.
-----------------------------------------------------------------------------------
Environments and abbreviation:
Production - prod
Development - dev
UAT         - uat
cloud-{env}-build-script.sh: 
    Script used to build and copy artifacts into nexus.altidev.net central server . On this server artifacts are stored at /home/realsvc/umg/{env}.
cloud-{env}-deployment-script.sh:
    Once artifacts are copied in nexus.altidev.net run this script to deploy artifacts to respective nodes.
cloud-{env}-monitoring-script.sh:    
    Script used to monitor machines and instances of java process.
    
mysql_script_prod_build.sh:
    Script used to upgrade database.