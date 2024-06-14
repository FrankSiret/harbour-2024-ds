# Distributed System & Clouds 

Harbour Space Lab

## Deploy

First build a docker image of the distributed-system and load-balancing app by running

```
mvn verify jib:dockerBuild -f ./distributedsystem/pom.xml

mvn verify jib:dockerBuild -f ./loadbalancing/pom.xml
```

Then run:

```
docker compose -f ./src/main/docker/app.yml up -d
```

## Deploy in EC2

First of all, you need an instance of EC2 running, connect to it using ssh in your favorite command prompt by running:

```sh
ssh -i your_private_key ubuntu@your_instance_ip
```

### Update and upgrade apt dependencies:

```sh
sudo apt-get update && sudo apt-get upgrade
```

### Install JDK 17 

The easiest way to install the JDK is using the `apt` package manager:

```sh
apt install openjdk-17-jdk openjdk-17-jre
```

Once installed, verify the Java version using the following command:

```sh
java -version
```

You should get the following output:

```
openjdk version "17.0.11" 2024-04-16
OpenJDK Runtime Environment (build 17.0.11+9-Ubuntu-1)
OpenJDK 64-Bit Server VM (build 17.0.11+9-Ubuntu-1, mixed mode, sharing)
```

### Install PostgreSQL

To install PostgreSQL, run the following command in the command prompt:

```sh
sudo apt install postgresql
```

#### Configure PostgreSQL

Now that we can connect to our PostgreSQL server, the next step is to set a password for the `postgres` user. Run the following command at a terminal prompt to connect to the default PostgreSQL template database:

```sh
sudo -u postgres psql template1
```

The above command connects to PostgreSQL database template1 as user `postgres`. Once you connect to the PostgreSQL server, you will be at an SQL prompt. You can run the following SQL command at the psql prompt to configure the password for the user postgres.

> Here we going to use the password `root` for testing propose.

```sql
ALTER USER postgres with encrypted password 'root';
```

Finally, you should restart the PostgreSQL service to initialize the new configuration. From a terminal prompt enter the following to restart PostgreSQL:

```sh
sudo systemctl restart postgresql.service
```

## Load Balancing

The Load Balancing Service is located at ./loadbalancing. Is designed to distribute incoming requests across multiple instances of a service to ensure high availability and reliability. This service monitors the health of each instance and dynamically adjusts the load distribution based on the availability of instances.

### Components

- **Health Check Mechanism**: Ensure that requests are only redirected to healthy instances.
- **List of Available Services**: Maintain and update a list of available service instances dynamically.
- **Load Balancing Algorithm**: Decide which instance should handle the next request.
- **Redirection Logic**: Redirect incoming requests to one of the available instances.

### How to use

Request to `POST http://localhost:9060/client/transaction` or `POST http://localhost:9060/client/transaction/cb`, will redirect to a healthy instances, the second one use the circuit breaker service implemented.

BODY:

```json
{
    "amount": 100.0,
    "currency": "USD",
    "description": "Hello, World",
    "userId": "user-1"
}
```

---

Request to `GET http://localhost:9060/routes` to get the list of available distributedsystem-core instances.

---

Additionally you can request to `POST http://localhost:9060/register` to register new instance manually

BODY:

```json
{
    "host": "hs-distributedsystem-app1",
    "port": "9000"
}
```

TODO: automatically register new instance to the loadbalancing

## Circuit Breaker

The Circuit Breaker Service is located inside of ./distributedsystem service. Is designed to enhance the resilience and reliability of a microservices-based system by preventing repeated failures from overwhelming the system. It monitors service calls and opens a circuit when a failure threshold is reached, temporarily halting further calls to allow the service to recover.

### Features 

- **Circuit Breaker Pattern**: Implemented using Resilience4j.
- **Retries with Exponential Backoff**: Retries failed requests with increasing wait times.
- **Jitter**: Adds randomness to retry intervals to avoid thundering herd problem.

### Configuration file

```yml
resilience4j:
  # this configure circuit breaker
  circuitbreaker:
    instances:
      my-circuit-breaker:
        sliding-window-type: COUNT_BASED # switches from CLOSED to OPEN if the last N calls failed or were slow
        sliding-window-size: 10 # the N from the last N calls for COUNT_BASED
        failure-rate-threshold: 50 # configure the failure rate threshold
        wait-duration-in-open-state: 40s # specifies the time that the circuit breaker should wait before switching to a half-open state
        permitted-number-of-calls-in-half-open-state: 5 # configures the number of calls that will be allowed in the half-open state

  # this configure circuit breaker retry
  retry:
    instances:
      my-circuit-breaker:
        max-attempts: 3 # maximum number of retry attempts that should be made for a failed operation 
                        # once this limit is reached, the operation is considered as failed
        wait-duration: 5s # the delay between each retry attempt, which can be a fixed delay or follow an exponential backoff strategy
        enable-exponential-backoff: true # increases the delay with each subsequent retry to avoid overloading the service
        exponential-backoff-multiplier: 2.0 # this multiplier is used in calculating the waiting time
                                            # wait_interval = base * (multiplier ^ (retry_count - 1)) +/- (random_interval)
        exponential-max-wait-duration: 60s # the upper limit for the wait duration between retries
                                           # once this limit is reached, subsequent retries will use this maximum duration
        enable-randomized-wait: true # enable jitter

  # this configure circuit breaker timeouts
  timelimiter:
    instances:
      my-circuit-breaker:
        timeout-duration: 60s # the maximum allowed duration for a microservice call
```