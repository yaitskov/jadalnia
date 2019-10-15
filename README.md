[![Build Status](https://travis-ci.org/yaitskov/jadalnia.svg?branch=master)](https://travis-ci.org/yaitskov/jadalnia)
[![Build Status](https://travis-ci.org/yaitskov/jadalnia.svg?branch=travis)](https://travis-ci.org/yaitskov/jadalnia)

#  Jadalnia application

Jadalnia application is designed for helping with conducting festivals.
The application provides online lines for food orders.

## Building

Build requires Db schema to be deployed.
MySql is provided by profile setup-env.
Db schema is deployed and upgraded via profile upgrade-db.
So the command for initial build is:
```
mvn clean install -P setup-env -P upgrade-db
```

The command above produces WAR
