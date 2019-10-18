#!/bin/bash

export JVM_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${PORT:-5001},server=y,suspend=n"

java -Djadalina.http.port=${HTTP_PORT:-8280} \
     $JVM_OPTS \
     -jar $(dirname $0)/server/target/server-1.0.0-SNAPSHOT.jar \
