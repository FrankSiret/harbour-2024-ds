# Distributed System & Clouds 

Harbour Space Lab

## Deploy

First build a docker image of your app by running

```
.\mvnw verify jib:dockerBuild
```

Then run:

```
docker compose -f ./src/main/docker/app.yml up -d
```