#!/bin/bash

DIR=`dirname "$(readlink -f "$0")"`
PLANTUML=$DIR/plantuml.jar

if [ -n "${JAVA_HOME}" ] && [ -x "${JAVA_HOME}/bin/java" ] ; then
    JAVA="${JAVA_HOME}/bin/java"
elif [ -x /usr/bin/java ] ; then
    JAVA=/usr/bin/java
else
    echo Cannot find JVM
    exit 1
fi

$JAVA -jar ${PLANTUML} "$DIR/classes_uml" "$DIR/sequence_uml"