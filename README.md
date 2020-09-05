# Spring Cloud Contract Demo

This is a demo to use Spring Cloud Contract to test Protobuf messages via Spring Cloud Stream RabbitMQ.

## Contracts
Switch to contracts project and run script below to install jar with contracts in local maven repository

```
./scripts generateStubs
```

## Consumer
Switch to consumer project and run tests

```
./gradlew test
```

## Producer
