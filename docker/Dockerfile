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
# Some fixes to get Tomcat7 running smooth on Ubuntu 14.04
RUN ln -s /var/lib/tomcat7/common /usr/share/tomcat7/common
RUN ln -s /var/lib/tomcat7/server /usr/share/tomcat7/server
RUN ln -s /var/lib/tomcat7/shared /usr/share/tomcat7/shared
# Install zip and unzip
RUN apt-get -y install zip
RUN apt-get -y install unzip
RUN mkdir /var/lib/Proton
RUN mkdir /var/lib/Proton/ProtonDefinitions/
WORKDIR /var/lib/Proton
# Clone Proton .war files
RUN wget https://github.com/ishkin/Proton/raw/master/artifacts/AuthoringTool.war
RUN wget https://github.com/ishkin/Proton/raw/master/artifacts/AuthoringToolWebServer.war
RUN wget https://github.com/ishkin/Proton/raw/master/artifacts/ProtonOnWebServer.war
RUN wget https://github.com/ishkin/Proton/raw/master/artifacts/ProtonOnWebServerAdmin.war
# Fix ProtonOnWebServerAdmin's ProtonAdmin.properties to point to a valid directory for definitions repository
RUN mkdir tmp
RUN unzip ProtonOnWebServerAdmin.war -d ./tmp
WORKDIR ./tmp
RUN sed -i -e 's/$//'	-e 's|^\(definitions-repository\)=.*|\1=/var/lib/Proton/ProtonDefinitions|' ProtonAdmin.properties
RUN zip ../ProtonOnWebServerAdmin.war *
WORKDIR /var/lib/Proton
RUN rm -rf tmp
# Ubuntu  Tomcat package sets CATALINA_HOME to this folder, and CATALINA_BASE to /var/lib/tomcat7. Proton expects them to be in the same folder.
#startup.sh and tomcat-users settings according to the Installation Guide
COPY ./startup.sh /usr/share/tomcat7/bin/
COPY ./setenv.sh /usr/share/tomcat7/bin/
COPY ./tomcat-users.xml /etc/tomcat7/
# Initial start of the tomcat server for deploying the apps
RUN service tomcat7 start || echo OK
#take wars from the artifacts directory 
RUN cp AuthoringTool.war /var/lib/tomcat7/webapps/AuthoringTool.war
RUN cp AuthoringToolWebServer.war /var/lib/tomcat7/webapps/AuthoringToolWebServer.war 
RUN cp ProtonOnWebServer.war /var/lib/tomcat7/webapps/ProtonOnWebServer.war
RUN cp ProtonOnWebServerAdmin.war /var/lib/tomcat7/webapps/ProtonOnWebServerAdmin.war
# install curl for smoketest
RUN apt-get -y install curl
# smoketest bash script
COPY ./docker_smoketest.sh /root/Proton/
ENV CATALINA_BASE /var/lib/tomcat7/
ENV CATALINA_HOME /usr/share/tomcat7/
# Changing definition via REST API effect only the file in /usr/share/tomcat7/webapps/ProtonOnWebServer/Proton.properties
RUN mkdir -p /usr/share/tomcat7/webapps/ProtonOnWebServer
RUN ln -s /var/lib/tomcat7/webapps/ProtonOnWebServer/Proton.properties /usr/share/tomcat7/webapps/ProtonOnWebServer/Proton.properties
CMD /usr/share/tomcat7/bin/catalina.sh run
