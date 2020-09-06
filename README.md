# Spring Cloud Contract Demo

This is a demo of Spring Cloud Contract to test Protobuf messages via Spring Cloud Stream RabbitMQ.

## Contracts
- switch to contracts project
- make changes to contracts based on consumer requirements
- run script below to install contract stubs jar in local maven repository

```
./scripts generateStubs
```

## Consumer
- switch to consumer project
- write contract consumer tests and use `stubTrigger` to generate messages based on the contract
- write implementation and make tests pass

```
./gradlew test
```

This part is complete, we used the API of the producer to make changes in the consumer.
Now file a pull request to the Contracts repository.

## Producer
TODO

## reference
Heavily based on this awesome [Spring Cloud Contract Samples Git Repo](https://cloud-samples.spring.io/spring-cloud-contract-samples/tutorials/contracts_external.html)
