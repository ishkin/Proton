#!/bin/sh

HOST=$1
PORT=$2

echo $1
echo $2

[ "$HOST" ] || [ "$IP" ] && HOST="$IP"
[ "$HOST" ] || HOST="localhost"
[ "$PORT" ] || PORT="8080"

at=`curl "http://$HOST:$PORT/ProtonOnWebServerAdmin/resources/instances/ProtonOnWebServer"`
echo $at
at2='{"state":"started","definitions-url":"\/ProtonOnWebServerAdmin\/resources\/definitions\/EmptyDefinition"}'
if [ "$at" != "$at2" ];
then
        echo "Proton instance not started sucessfully"
        exit 1
fi

echo "Smoke test ran successful."
exit 0
