
JAVA_OPS="-server
-XX:+UseCompressedOops
-XX:-UseBiasedLocking
-Xmx4096M
-Xms4096M
-XX:MetaspaceSize=256M
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-XX:+ParallelRefProcEnabled
-XX:ErrorFile=./hs_err_pid%p.log
-Xloggc:./gc.log
-verbose:gc
-XX:HeapDumpPath=./dump.log
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+HeapDumpOnOutOfMemoryError
-XX:+DisableExplicitGC
-Dserver.port=8080
-Dio.netty.net.somaxconn.trySysctl=true"

java $JAVA_OPS -jar jarkata-dts-server-1.0-SNAPSHOT.jar &
