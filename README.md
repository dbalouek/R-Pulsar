# Rutgers-Pulsar
R-Pulsar is an IoT Edge Framework, that extends cloud capabilities to local devices and provides a programming model for deciding what, when, and where data get collected and processed. It has been deployed and tested on embedded devices (Raspberry Pi and Android phone) and presents an experimental evaluation that demonstrates that R-Pulsar can enable timely stream analytics by effectively leveraging edge and core resources.

## Documentation

[R-Pulsar Documentation on Github Pages](https://dbalouek.github.io/R-Pulsar/)

## Docker

Build and run R-Pulsar Hello World example (`master`, `producer`, and `consumer`):
```
docker-compose up --build
```

Clean up:
```
docker-compose down
```

## Smoke Detector Example
The [Smoke Detector Application](https://github.com/iperezx/sage-smoke-detection) example generates an AI inference on an image. The plugin can be configured to use two different AI models and three different camera inputs to run on [Sage](https://docs.sagecontinuum.org/docs/about/overview), Docker, or Kubernetes. Resort to the Github Repository for full usage: [Smoke Detector Application](https://github.com/iperezx/sage-smoke-detection).

Currently, the `producer` is reading the output from the Smoke Detector application (`data.ndjson`) and publish the data to `master` so that a `consumer` can consume the data.


Build R-Pulsar with Smoke Detector example using an overlay:
```
docker-compose -f docker-compose.yaml -f docker-compose-smoke-overlay.yaml up --build
```

Clean up:
```
docker-compose down --remove-orphans
```


## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details




