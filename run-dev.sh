#!/bin/bash

export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${PORT:-5001},server=y,suspend=n"

mvn -pl server -P run-server -DskipTests install
