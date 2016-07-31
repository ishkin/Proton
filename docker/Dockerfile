# Update tomcat
FROM tomcat
RUN apt-get update && apt-get install -y bash curl
WORKDIR /
ADD tomcat-users.xml /usr/local/tomcat/conf
ADD https://raw.githubusercontent.com/ishkin/Proton/master/artifacts/AuthoringTool.war /usr/local/tomcat/webapps
ADD https://raw.githubusercontent.com/ishkin/Proton/master/artifacts/AuthoringToolWebServer.war /usr/local/tomcat/webapps
ADD https://raw.githubusercontent.com/ishkin/Proton/master/artifacts/ProtonOnWebServer.war /usr/local/tomcat/webapps
ADD https://raw.githubusercontent.com/ishkin/Proton/master/artifacts/ProtonOnWebServerAdmin.war /usr/local/tomcat/webapps

EXPOSE 8080
# smoketest bash script
RUN mkdir -p /root/Proton/
ADD https://raw.githubusercontent.com/ishkin/Proton/master/docker/docker_smoketest.sh /root/Proton/docker_smoketest.sh
RUN chmod +x /root/Proton/docker_smoretest.sh
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh", "run"]
