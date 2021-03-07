#!/bin/bash

DTS_HOME=/Users/vkata/code/jarkata-dts/jarkata-dts-build/target/jarkata-dts

JAVA_OPS="-server
-XX:+UseCompressedOops
-XX:-UseBiasedLocking
-XX:+AlwaysPreTouch
-Xmx3g
-Xms3g
-XX:MetaspaceSize=256M
-XX:+UseG1GC
-XX:G1HeapRegionSize=16m
-XX:G1ReservePercent=25
-XX:InitiatingHeapOccupancyPercent=30
-XX:MaxGCPauseMillis=200
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=5
-XX:GCLogFileSize=30m
-XX:ErrorFile=$DTS_HOME/logs/hs_err_pid%p.log
-Xloggc:$DTS_HOME/logs/gc/gc.log
-verbose:gc
-XX:HeapDumpPath=$DTS_HOME/logs/dump
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+HeapDumpOnOutOfMemoryError
-XX:+DisableExplicitGC
-DLOG_PATH=$DTS_HOME/logs
-Dio.netty.net.somaxconn.trySysctl=true"

java $JAVA_OPS -classpath $DTS_HOME/config:$DTS_HOME/jarkata-dts.jar  cn.jarkata.dts.DTSStarter &
