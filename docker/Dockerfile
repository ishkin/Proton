FROM ubuntu:latest
# Update Ubuntu
RUN apt-get update && apt-get -y upgrade
# Add oracle java 7 repository
RUN apt-get -y install software-properties-common
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get -y update
# Accept the Oracle Java license
RUN echo "oracle-java7-installer shared/accepted-oracle-license-v1-1 boolean true" | debconf-set-selections
# Install Oracle Java
RUN apt-get -y install oracle-java7-installer
# Install tomcat
RUN apt-get -y install tomcat7 tomcat7-admin
RUN echo "JAVA_HOME=/usr/lib/jvm/java-7-oracle" >> /etc/default/tomcat7
EXPOSE 8080
# Install git
RUN apt-get -y install git
WORKDIR /root
# Clone Proton repo
RUN git clone https://github.com/ishkin/Proton.git
# Ubuntu  Tomcat package sets CATALINA_HOME to this folder, and CATALINA_BASE to /var/lib/tomcat7. Proton expects them to be in the same folder.
# This is a hack in order to get Proton running on Ubuntu official Tomcat packages
# This is suboptimal, in case Proton.properties or JSON application files change syntax, the builds would fail and these files should also be updated.
RUN mkdir /usr/share/tomcat7/webapps
RUN mkdir /usr/share/tomcat7/webapps/ProtonOnWebServer
COPY ./Proton.properties /usr/share/tomcat7/webapps/ProtonOnWebServer/
COPY ./DoSAttack2.json /usr/share/tomcat7/webapps/ProtonOnWebServer/
#startup.sh and tomcat-users settings according to the Installation Guide
COPY ./startup.sh /usr/share/tomcat7/bin/
COPY ./tomcat-users.xml /etc/tomcat7/
# Initial start of the tomcat server for deploying the apps
RUN service tomcat7 start || echo OK
RUN apt-get -y install unzip
WORKDIR /root/Proton/IBM Proactive Technology Online
RUN unzip -d IBMProtonOnWebServer IBMProtonOnWebServer.zip 
WORKDIR /root/Proton/IBM Proactive Technology Online/IBMProtonOnWebServer
RUN cp *war /var/lib/tomcat7/webapps/
RUN mkdir /root/docker
WORKDIR /root
# install curl for smoketest
RUN apt-get -y install curl
# smoketest bash script
COPY ./docker_smoketest.sh /root/docker/
ENV CATALINA_BASE /var/lib/tomcat7/
ENV CATALINA_HOME /usr/share/tomcat7/
CMD /usr/share/tomcat7/bin/catalina.sh run