# Distributed System & Clouds 

Harbour Space Lab

## Deploy

First build a docker image of the distributed-system and load-balancing app by running

```
mvn verify jib:dockerBuild -f .\distributedsystem\pom.xml

mvn verify jib:dockerBuild -f .\loadbalancing\pom.xml
```

Then run:

```
docker compose -f ./src/main/docker/app.yml up -d
```
