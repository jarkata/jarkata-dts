FROM centos
RUN yum install -y java-1.8.0-openjdk.x86_64
EXPOSE  8080
WORKDIR /opt
