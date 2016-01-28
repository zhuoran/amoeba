#!/bin/bash

JMX_PORT=`expr $1 + 500`

JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xmn384m -XX:MaxPermSize=256m \
-Xss256k -XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC -XX:CMSFullGCsBeforeCompaction=5 \
-XX:+UseCMSCompactAtFullCollection \
-XX:+PrintGC -Xloggc:/letv/logs/apps/api/whatslive/gc_$1.log"

#-Djava.rmi.server.hostname= \
#-Dcom.sun.management.jmxremote \
#-Dcom.sun.management.jmxremote.port=$JMX_PORT \
#-Dcom.sun.management.jmxremote.authenticate=false \
#-Dcom.sun.management.jmxremote.ssl=false"

PHOME=$(dirname `readlink -f "$0"`)
PHOME=$(dirname $PHOME)

pid=`ps -eo pid,args | grep amoeba | grep $1 | grep java | grep -v grep | awk '{print $1}'`

if [ -n "$pid" ]
then
    kill -3 ${pid}
    kill ${pid} && sleep 3
    if [  -n "`ps -eo pid | grep $pid`" ]
    then
        kill -9 ${pid}
    fi
    echo "kill pid: ${pid}"
fi

java -Dio.netty.leakDetectionLevel=advanced -Dmodule=$1 $JAVA_OPTS -cp $PHOME/conf:$PHOME/lib/* me.zhuoran.amoeba.example.Main $1 > /dev/null 2>&1 &
