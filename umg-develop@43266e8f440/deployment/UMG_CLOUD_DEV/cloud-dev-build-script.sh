rm umg-me2.war
rm umg-runtime.war
rm modelet-0.0.1-SNAPSHOT.one-jar.jar
rm umg-admin.war
cd ../umg/branches/UMG_1052_ADAUTH_11SEP/
mvn clean
mvn clean package -DskipTests=true -Denv=aws-dev -PAll
cd ../../../UMG_CLOUD_DEV
cp ../umg/branches/UMG_1052_ADAUTH_11SEP/me2/target/umg-me2.war .
cp ../umg/branches/UMG_1052_ADAUTH_11SEP/umg-runtime/target/umg-runtime.war .
cp ../umg/branches/UMG_1052_ADAUTH_11SEP/modelet/target/modelet-0.0.1-SNAPSHOT.one-jar.jar .
cp ../umg/branches/UMG_1052_ADAUTH_11SEP/umg-admin/sdc/web-ui/target/umg-admin.war .

#scp umg-me2.war realsvc@nexus.altidev.net:/home/realsvc/umg/dev
#scp umg-runtime.war realsvc@nexus.altidev.net:/home/realsvc/umg/dev
scp umg-admin.war realsvc@nexus.altidev.net:/home/realsvc/umg/dev
#scp modelet-0.0.1-SNAPSHOT.one-jar.jar realsvc@nexus.altidev.net:/home/realsvc/umg/dev
#scp modelet.sh realsvc@nexus.altidev.net:/home/realsvc/umg/dev

#scp cloud-dev-deployment-script.sh realsvc@nexus.altidev.net:/home/realsvc/umg/dev



