#!/bin/bash
DTS_DIR=$(cd $(dirname $0); pwd)
DTS_HOME=$DTS_DIR/..
echo 'DTS_HOME='$DTS_HOME
DTS_CLASS_PATH=$DTS_HOME/config:$DTS_HOME/lib/jarkata-dts.jar
echo 'DTS_CLASS_PATH='$DTS_CLASS_PATH
java -classpath $DTS_CLASS_PATH cn.jarkata.dts.DTShutdown >$DTS_HOME/sys.log 2>&1 &
