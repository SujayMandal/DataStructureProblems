#! /bin/sh

#export CATALINA_OPTS="$CATALINA_OPTS -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true"

export CATALINA_OPTS="$CATALINA_OPTS -Xms2048m -Xmx4096m"

#export CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

