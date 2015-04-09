#!/bin/sh

service tomcat7 start
at=`curl -s localhost:8080/ProtonOnWebServerAdmin/resources/instances/ProtonOnWebServer`
at2='{"state":"started","definitions-url":"\/ProtonOnWebServerAdmin\/resources\/definitions\/DoSAttack2"}'
if [ "$at" != "$at2" ];
then
        echo "Proton instance not started sucessfully"
        exit 1
fi

echo "Smoke test ran successful."
exit 0
