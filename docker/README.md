# How to install the Proactive Technology Online with Docker

You can install the Proactive Technology Online very easily using docker. 

Follow these steps:
1.  Update `/etc/hosts` to add the current machine as a hostname.
2.	Install docker. 
3.	Navigate to a folder where you’d like the docker files to be placed.
4.	Download the docker files to this new folder from the [GitHub Proton repository](https://github.com/ishkin/Proton/tree/master/docker).

5.	Build the Dockerfile.

    This can be done in the following way:
    
        sudo docker build –t proton .
        sudo docker daemon & # THIS NEEDS TO BE RUN ONLY ONCE, IN THE BACKGROUND

6.	Run the docker image

    This can be done in the following way:
        
        sudo docker run --privileged=true --cap-add SYS_PTRACE -p 8080:8080 -it -d proton tail -f /dev/null
    
    The `tail –f /dev/null` is the command to execute inside the container, and it is used to force the container to continue running and not shut down immediately after starting.
    
To access the container and run commands from within it:

        sudo docker ps
        sudo docker exec -it <container_id taken from ps results> bash

It is important to note that Tomcat is not started automatically inside the container, and thus needs to be started manually. In order to do that, first access the container using the above command and then do:

        service tomcat7 start
        
        service tomcat7 status # THIS SHOULD RETURN: Starting Tomcat servlet engine tomcat7
