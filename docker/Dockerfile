# Update Ubuntu
FROM ubuntu:latest
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y wget bash curl
RUN wget https://raw.githubusercontent.com/ishkin/Proton/master/puppet/miscellaneous/CEP_Install_via_puppet.sh
RUN chmod o+x /CEP_Install_via_puppet.sh
RUN bash /CEP_Install_via_puppet.sh

RUN apt-get clean
EXPOSE 8080
# smoketest bash script
RUN mkdir -p /root/Proton/
RUN wget https://raw.githubusercontent.com/ishkin/Proton/master/docker/docker_smoketest.sh -q -O /root/Proton/docker_smoketest.sh
