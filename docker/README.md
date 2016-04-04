# How to install the Proactive Technology Online with Docker

## Build your docker image

You can install the Proactive Technology Online very easily using docker. 

Follow these steps:

1.  Update `/etc/hosts` to add the current machine as a hostname.

2.	Install docker. 
3.	Start the docker daeomon - either during the docker installationve, or with the command

            sudo docker daemon & # THIS NEEDS TO BE RUN ONLY ONCE, IN THE BACKGROUND
    
    Or you can use the "Docker Quickstart Terminal" application.

4.	Navigate to a folder where you’d like the docker files to be placed.

5.	Download the docker files to this new folder from the [GitHub Proton repository](https://github.com/ishkin/Proton/tree/master/docker). You can do that with the SVN command

            svn checkout https://github.com/ishkin/Proton.git/trunk/docker

6.	Build the Docker Image from the Dockerfile:

            sudo docker build –t proton .

7.	Run the docker image you just generated:

            sudo docker run --privileged=true --cap-add SYS_PTRACE -p 8080:8080 -it -d proton tail -f /dev/null
    
    The `tail –f /dev/null` is the command to execute inside the container, and it is used to force the container to continue running and not shut down immediately after starting.
    
8. We still need to fix a problem with initializing the Proton apps in the tomcat server. For that, we need to access the container and run commands from within it:

            sudo docker ps # That will provide <container_id> for the proton image.
            sudo docker exec -it <container_id> bash

9. We need to remove these apps as the automatic installation did not do the well:

            cd /var/lib/tomcat7/webapps
            rm -rf AuthoringTool AuthoringToolWebServer ProtonOnWebServer ProtonOnWebServerAdmin

10. Ensure the Tomcat server is started.
      If the docker image does not start Tomcat automatically, we need to do that. You can use the second command to figure out tomcat7 status and if running, no need to start it again, but starting an already started service is harmless:

            service tomcat7 start
            service tomcat7 status # THIS SHOULD RETURN: Starting Tomcat servlet engine tomcat7

11. Try it.
     * Find the correct ip of your docker container on your network and try http://\<ip\>:\<port\>. That will display the tomcat "It Works!" welcome screen.
     * Try also the Web Rules Authoring Tool with http://\<ip\>:\<port\>/AuthoringTool, which should repsond with the Authoring Tool web screen.

## Pull a ready docker image from the docker hub

Using the ready image in the [Proton Docker Hub](https://hub.docker.com/r/fiware/proactivetechnologyonline/). Work with this modified step 7 and follow on with the remaining steps:

(7). Run the docker image from the docker hub:

            sudo docker run --privileged=true --cap-add SYS_PTRACE -p 8080:8080 -it -d fiware/proactivetechnologyonline tail -f /dev/null
    
    
