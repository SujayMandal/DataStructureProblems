rm umg-me2.war
rm umg-runtime.war
rm modelet.one-jar.jar
rm umg-admin.war
cd ../umg/branches/2.1.0-RELEASE-IAM
mvn clean
mvn clean package -DskipTests=true -Denv=aws-perf -PAll
cd ../../../UMG_CLOUD_PERF
cp ../umg/branches/2.1.0-RELEASE-IAM/me2/target/umg-me2.war .
cp ../umg/branches/2.1.0-RELEASE-IAM/umg-runtime/target/umg-runtime.war .
cp ../umg/branches/2.1.0-RELEASE-IAM/modelet/target/modelet.one-jar.jar .
cp ../umg/branches/2.1.0-RELEASE-IAM/umg-admin/sdc/web-ui/target/umg-admin.war .

#scp umg-me2.war realsvc@nexus.altidev.net:/home/realsvc/umg/perf
#scp umg-runtime.war realsvc@nexus.altidev.net:/home/realsvc/umg/perf
scp umg-admin.war realsvc@nexus.altidev.net:/home/realsvc/umg/perf
#scp modelet.one-jar.jar realsvc@nexus.altidev.net:/home/realsvc/umg/perf
#scp setenv.sh realsvc@nexus.altidev.net:/home/realsvc/umg/perf
#scp modelet_4.sh realsvc@nexus.altidev.net:/home/realsvc/umg/perf


