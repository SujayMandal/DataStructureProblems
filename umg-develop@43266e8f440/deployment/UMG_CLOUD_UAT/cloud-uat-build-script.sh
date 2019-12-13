rm umg-me2.war
rm umg-runtime.war
rm modelet-*.jar
rm umg-admin.war
cd ../umg/branches/2.1.0-RELEASE-IAM
mvn clean
mvn clean package -DskipTests=true -Denv=aws-uat -PAll
cd ../../../UMG_CLOUD_UAT
cp ../umg/branches/2.1.0-RELEASE-IAM/me2/target/umg-me2.war .
cp ../umg/branches/2.1.0-RELEASE-IAM/umg-runtime/target/umg-runtime.war .
cp ../umg/branches/2.1.0-RELEASE-IAM/modelet/target/modelet.one-jar.jar .
cp ../umg/branches/2.1.0-RELEASE-IAM/umg-admin/sdc/web-ui/target/umg-admin.war .

scp umg-me2.war realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp umg-runtime.war realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp umg-admin.war realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp modelet.one-jar.jar realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp setenv.sh realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp modelet.sh realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp modelet_8.sh realsvc@nexus.altidev.net:/home/realsvc/umg/uat
scp cloud-uat-deployment-script.sh realsvc@nexus.altidev.net:/home/realsvc/umg/uat

