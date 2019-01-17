## Spring Cloud Data Flow - Prometheus Service Discovery (Local Deployer)

Sample service that uses the SCDF's runtime applications metadata to discover SpringBoot applications to be monitored with Prometheus.

Works only for SCDF local deployer and should be used on for test/demo purposes. 


```
./mvnw clean install docker:build -Pspring
./mvnw -Ddocker.username=xxx -Ddocker.password=xxx clean install docker:push -Pspring
```
