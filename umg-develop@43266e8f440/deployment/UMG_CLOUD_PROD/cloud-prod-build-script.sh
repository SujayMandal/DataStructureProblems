rm umg-me2.war
rm umg-runtime.war
rm modelet-*.jar
rm umg-admin.war
cd ../umg/branches/2.0.0-RELEASE-IAM
mvn clean
mvn clean package -DskipTests=true -Denv=aws-prod -PAll
cd ../../../UMG_CLOUD_PROD
cp ../umg/branches/2.0.0-RELEASE-IAM/me2/target/umg-me2.war .
cp ../umg/branches/2.0.0-RELEASE-IAM/umg-runtime/target/umg-runtime.war .
cp ../umg/branches/2.0.0-RELEASE-IAM/modelet/target/modelet-2.0.0-RELEASE.one-jar.jar .
cp ../umg/branches/2.0.0-RELEASE-IAM/umg-admin/sdc/web-ui/target/umg-admin.war .

scp umg-me2.war realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp umg-runtime.war realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp umg-admin.war realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp modelet.one-jar.jar realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp setenv.sh realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp modelet.sh realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp modelet_8.sh realsvc@nexus.altidev.net:/home/realsvc/umg/prod
scp cloud-prod-deployment-script.sh realsvc@nexus.altidev.net:/home/realsvc/umg/prod

