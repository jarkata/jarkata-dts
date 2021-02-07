
JAVA_OPS="-server
-XX:+UseCompressedOops
-XX:-UseBiasedLocking
-XX:+AlwaysPreTouch
-Xmx8g
-Xms8g
-XX:MetaspaceSize=256M
-XX:+UseG1GC
-XX:G1HeapRegionSize=16m
-XX:G1ReservePercent=25
-XX:InitiatingHeapOccupancyPercent=30
-XX:MaxGCPauseMillis=200
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=5
-XX:GCLogFileSize=30m
-XX:ErrorFile=./logs/hs_err_pid%p.log
-Xloggc:./logs/gc/gc.log
-verbose:gc
-XX:HeapDumpPath=./logs/dump
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+HeapDumpOnOutOfMemoryError
-XX:+DisableExplicitGC
-Dio.netty.net.somaxconn.trySysctl=true
-Dserver.port=8089
-Dlog.sample=500
-Dwork.threads=600"

java $JAVA_OPS -jar jarkata-dts-server.jar &
