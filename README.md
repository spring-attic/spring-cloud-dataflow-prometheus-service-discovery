## Spring Cloud Data Flow - Prometheus Service Discovery (Local Deployer)

Sample service that uses the SCDF's runtime applications metadata to discover SpringBoot applications to be monitored with Prometheus.

> Works only for SCDF local deployer and should be used only for test/demo purposes! 

Prometheus Service Discovery for SCSt apps deployed with the Local Deployer. 
It is build on top of the [file_sd_config](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#%3Cfile_sd_config%3E) mechanism.


### Local Mode 

> `SCDF_HOST_IP` should point to the actual IP of the host where SCDF runs (127.0.0.1 would not work in docker-compose settings)

```bash
export SCDF_HOST_IP=Your-Local-Host-IP

java -jar spring-cloud-dataflow-prometheus-service-discovery.jar \
   --metrics.prometheus.target.discoveryUrl=http://${SCDF_HOST_IP}:9393/runtime/apps \
   --metrics.prometheus.target.filePath=/tmp/scdf-targets/targets.json \
   --metrics.prometheus.target.cron=0/30 * * * * * \
   --metrics.prometheus.target.mode=local \
   --metrics.prometheus.target.overrideIp=${SCDF_HOST_IP}
```

### PromRegator Mode

```bash
java -jar spring-cloud-dataflow-prometheus-service-discovery.jar \
  --metrics.prometheus.target.discoveryUrl=http://localhost:8080/discovery \
  --metrics.prometheus.target.filePath=/tmp/targets.json \
  --metrics.prometheus.target.cron=0/30 * * * * * \
  --metrics.prometheus.target.mode=promregator \
  --server.port=9091
```


### Build

```
./mvnw clean install docker:build -Pspring
./mvnw -Ddocker.username=xxx -Ddocker.password=xxx clean install docker:push -Pspring
```
