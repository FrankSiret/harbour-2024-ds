#!/bin/sh

echo "Starting..."
exec java -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.hs.loadbalancing.LoadBalancingApplication"  "$@"
