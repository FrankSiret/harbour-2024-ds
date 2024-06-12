#!/bin/sh

echo "Starting..."
exec java -Djava.security.egd=file:/dev/./urandom -jar "${HOME}/app.jar" "$@"
