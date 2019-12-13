rm umg-me2.war
rm umg-runtime.war
rm modelet.one-jar.jar
rm umg-admin.war
cd ../umg/branches/2.1.0-RELEASE-IAM
mvn clean
mvn clean package -DskipTests=true -Denv=aws-stress -PAll
cd ../../../UMG_CLOUD_QE_STRESS
cp ../umg/branches/2.1.0-RELEASE-IAM/me2/target/umg-me2.war .
cp ../umg/branches/2.1.0-RELEASE-IAM/umg-runtime/target/umg-runtime.war .
cp ../umg/branches/2.1.0-RELEASE-IAM/modelet/target/modelet.one-jar.jar .
cp ../umg/branches/2.1.0-RELEASE-IAM/umg-admin/sdc/web-ui/target/umg-admin.war .
