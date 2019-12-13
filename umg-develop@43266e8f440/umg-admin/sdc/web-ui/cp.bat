set echo on
set CATALINA_HOME=D:\tomcat732
set PROJECT_HOME=D:\wsspace1\umg
set APP_NAME=umg_admin
call %CATALINA_HOME%\bin\catalina.bat stop
echo Stopped tomcat
rmdir /s /q %CATALINA_HOME%\webapps\%APP_NAME%
echo Removed application
echo %WAR_HOME%
copy %PROJECT_HOME%\umg-admin\sdc\web-ui\target\umg-admin.war %CATALINA_HOME%\webapps\
echo Copied application
call %CATALINA_HOME%\bin\catalina.bat start
echo Started tomcat