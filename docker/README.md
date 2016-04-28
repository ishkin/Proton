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

            sudo docker run --name=proton -p 8080:8080 -it -d proton
    
    
8. We can login to the image:

            sudo docker ps # That will provide <container_id> for the proton image. That id is also "proton" if the --name=proton param was used in the docker run command.
            sudo docker exec -it <container_id> bash
            
9. Try it.
     * If your computer supports the port mapping (not working on MacOs w/out some tweaking), you can check that tomcat works with the link https://localhost:8080. Otherwise, you need to find the correct ip of your docker container image, and test with http://\<ip\>:8080. That will display the tomcat welcome screen.
     * Try also the Web Rules Authoring Tool with http://localhost:8080/AuthoringTool, which should repsond with the Authoring Tool web screen.

## Notes for working with MacOs.

Docker should start with boot2docker:
 
            boot2docker up          # startup the docker daemon
            eval "$(boot2docker shellinit)"; # set up environment variables
            boot2docker ip          # obtain docker image ip on your machine
            # After starting your image (docker run command - see above), run the next command to set up the port mapping:
            VBoxManage controlvm "boot2docker-vm" natpf1 "tcp-port8080,tcp,,8080,,8080";
            # Now you can try tomcat with this command:
            open http://localhost:8080

## Pull a ready docker image from the docker hub

Using the ready image in the [Proton Docker Hub](https://hub.docker.com/r/fiware/proactivetechnologyonline/).

(7). Run the docker image from the docker hub:

            sudo docker run --name=proton -p 8080:8080 -it -d proton
    
    
