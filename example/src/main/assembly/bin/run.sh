#!/bin/bash

PARAM=$1
INDEX=0
if [[ -z "$1" ]]; then
    PARAM=8002
fi

SCRIPT_HOME=$(dirname $(readlink -f $0))
for i in $(seq 0 $INDEX)
do
    PORT=`expr $PARAM + $i`
    bash -ex $SCRIPT_HOME/startup.sh $PORT
done