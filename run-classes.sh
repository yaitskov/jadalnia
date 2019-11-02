#!/bin/bash

export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${PORT:-5001},server=y,suspend=n"

mvn exec:java -pl :server -Djadalina.http.port=${HTTP_PORT:-8280} \
    -Dweb.push.key.private=$WEB_PUSH_KEY_PRIVATE \
    -Dexec.mainClass=org.dan.jadalnia.StartJadalniaKt
