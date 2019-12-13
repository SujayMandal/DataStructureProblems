#! /bin/sh

export CATALINA_OPTS="$CATALINA_OPTS -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true"

export CATALINA_OPTS="$CATALINA_OPTS -Xms1024m -Xmx2048m"
