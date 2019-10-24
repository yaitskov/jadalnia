#!/bin/bash

rm dist/{*.br,*.gz}
webpack-cli
cp $(dirname $0)/src/worker/worker-101.js $(dirname $0)/dist
