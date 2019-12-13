## To update the database with new change log
mvn liquibase:update

## To rollback to a specific version tag
mvn liquibase:rollback -Dliquibase.rollbackTag=<<version tag>>

## To generate update script from the last db state. SQL will be generated at <path>\db\target\liquibase\migrate.sql
mvn liquibase:updateSQL

## To generate rollback script to a specific version tag. SQL will be generated at <path>\db\target\liquibase\migrate.sql
mvn liquibase:rollbackSQL -Dliquibase.rollbackTag=<<version tag>>

liquibase
    --driver=com.mysql.jdbc.Driver
    --classpath=C:/Drive/Apps/apache-maven-3.5.0/repository/mysql/mysql-connector-java/5.1.31/mysql-connector-java-5.1.31.jar
    --changeLogFile=C:/Drive/DIY/Liquibase/databaseChangeLog.sql
    --url="jdbc:mysql://localhost/liquibase"
    --username=root
    --password=""
        update

liquibase --driver=com.mysql.jdbc.Driver --classpath=C:/Drive/Apps/apache-maven-3.5.0/repository/mysql/mysql-connector-java/5.1.31/mysql-connector-java-5.1.31.jar --changeLogFile=C:/Drive/DIY/Liquibase/changelog.xml --url="jdbc:mysql://localhost/liquibase" --username=root --password="" rollback tag

