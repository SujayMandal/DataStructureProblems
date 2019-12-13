set CATALINA_HOME=D:\tomcat732
d:\tomcat732\bin\shutdown.bat
rmdir /S /Q d:\tomcat732\webapps\umg-admin
del d:\tomcat732\webapps\umg-admin.war
copy target\umg-admin.war d:\tomcat732\webapps
d:\tomcat732\bin\catalina.bat jpda start

