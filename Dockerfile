FROM centos
RUN yum install -y java-1.8.0-openjdk.x86_64
EXPOSE  22
EXPOSE  8080
WORKDIR /opt
#ADD ./jarkata-dts-server/target/jarkata-dts-server-1.0-SNAPSHOT.jar
