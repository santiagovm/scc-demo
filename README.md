# Spring Cloud Contract Demo

This is a demo of Spring Cloud Contract with Protobuf-based messages via Spring Cloud Stream and RabbitMQ.

## Introduction
The awesome [Spring Cloud Contract Samples Git Repo](https://github.com/spring-cloud-samples/spring-cloud-contract-samples) heavily inspired this demo.

There are three components:

- Producer: sends messages to consumer indicating whether someone is old enough to drink beer.
- Consumer: processes age verification messages from the producer
- Contracts: defines the contracts between consumer and producer

There are multiple ways in Spring Cloud Contract to share contracts between consumers and producers. 
This demo uses the [Contracts in an External Repository approach](https://cloud.spring.io/spring-cloud-contract/reference/html/using.html#flows-cdc-contracts-external)

At a high level, the implementation flow looks like this:

1. The consumer works with the contract definitions from the separate repository
1. Once the consumer’s work is done, a branch with working code is done on the consumer side and a pull request is made to the separate repository that holds the contract definitions.
1. The producer takes over the pull request to the separate repository with contract definitions and installs the JAR with all contracts locally.
1. The producer generates tests from the locally stored JAR and writes the missing implementation to make the tests pass.
1. Once the producer’s work is done, the pull request to the repository that holds the contract definitions is merged.
1. After the CI tool builds the repository with the contract definitions and the JAR with contract definitions gets uploaded to Nexus or Artifactory, the producer can merge its branch.
1. Finally, the consumer can switch to working online to fetch stubs of the producer from a remote location, and the branch can be merged to master.

## Defining Contracts
In the demo, contracts are defined using Spring Cloud Contract's Groovy DSL, see below an example:

```groovy
org.springframework.cloud.contract.spec.Contract.make {
    description("""
Sends a positive verification message when person is eligible to get a beer

\```
given:
    client is old enough
when:
    he applies for a beer
then:
    we'll send a message with a positive verification
\```  
""")
    // label by means of which the output message can be triggered
    label 'accepted_verification'

    // method in producer that triggers the output message
    input {
        triggeredBy("clientIsOldEnough()")
    }

    // output message of the contract
    outputMessage {
        // destination to which the output message will be sent to
        sentTo 'verifications'

        // the body of the output message
        body(fileAsBytes("temp/encoded-messages/response-old-enough.bin"))
    }
}
```

Notice contract references a file to be loaded as bytes for the message body: `temp/encoded-messages/response-old-enough.bin`, 
this binary file is auto-generated at build time from a text file using Protobuf text-format under `contracts/src/main/resources/contracts/com/vasquez/beer-api-producer-external/beer-api-consumer/messages`. 
Text format is not very well documented but there is some decent information [here](https://stackoverflow.com/a/18877167/725987) and [here](https://github.com/protocolbuffers/protobuf/blob/master/java/compatibility_tests/v2.5.0/tests/src/main/java/com/google/protobuf/test/TextFormatTest.java).
See below an example of the message body in Protobuf text format:

```
name: "santiago vasquez"
status: OK
beersCount: 7
city: "medellin"
dob: 165585600
```

The corresponding proto file is:

```proto
syntax = "proto3";

package com.vasquez.beer;

option java_package = "com.vasquez";
option java_outer_classname = "BeerProtos";
option java_multiple_files = true;

message Response {
    enum BeerCheckStatus {
        NOT_OK = 0;
        OK = 1;
    }

    string name = 1;
    BeerCheckStatus status = 2;
    int32 beersCount = 3;

    string city = 16;
    int64 dob =17;
}
```

## Generating Stubs for Consumers
After making changes to contracts as described in the previous section is time for the consumer to generate stubs 
from the contracts. These stubs will be used later when running tests in the consumer codebase. 
To generate the stubs go to the contracts project and run the following script:

```shell script
$ ./scripts/generate-stubs-for-consumer.sh
```

The previous script will create a JAR in your local Maven repository. To clean it, run the following script:

```shell script
$ ./scripts/clean-all.sh
```

## Running Contract Tests in the Consumer Side
To enable the Spring Cloud Contract Stub Runner in your tests, annotate the test class 
with `@AutoConfigureStubRunner` indicating the JAR that contains the stubs (which was generated in the previous step) 

```java
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, 
    ids = "com.vasquez:beer-api-producer-external")
public class BeerVerificationListenerTest {
    
    // stuff goes here ...
}
```

In the test trigger the stub to send a message as defined in the contract and verify the consumer made 
the expected calls based on the message received. Notice in the example below the label used to trigger 
the message matches the one defined in the contract `accepted_verification`

```java
    @Test
    public void should_call_beer_verification_service_when_verification_was_accepted() {

        // act
        this.stubTrigger.trigger("accepted_verification");

        // assert
        BeerVerification verification = beerVerificationServiceMock.getVerificationInRequest();

        assertThat(verification, is(notNullValue()));

        assertThat(verification.getName(), is(equalTo("santiago vasquez")));
        assertThat(verification.getIsApproved(), is(equalTo(true)));
        assertThat(verification.getBeersCount(), is(equalTo(7)));
        assertThat(verification.getCity(), is(equalTo("medellin")));
        assertThat(verification.getDateOfBirth(), is(equalTo(Instant.parse("1975-04-01T12:00:00Z"))));
    }
```

Run command below from the consumer folder to run consumer tests: 

```shell script
$ ./mvnw test
```

### Spring Cloud Contract Bug Notice
There is an open bug in Spring Cloud Contract that prevents the consumer tests in the demo to work properly. 
The Spring Cloud Contract Stub Runner gets a stack overflow exception when trying to load the message payload 
as bytes. For more details see [this](https://github.com/spring-cloud/spring-cloud-contract/issues/1404).

## Running Contract Tests in the Producer Side
The producer project is configured to use the contract's JAR to generate tests. Below is an extract from the project's POM file showing this.

```xml
            <plugin>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-contract-maven-plugin</artifactId>
                <version>${spring-cloud-contract.version}</version>
                <extensions>true</extensions>
                <configuration>
                    <baseClassMappings>
                        <baseClassMapping>
                            <contractPackageRegex>.*beer*.*</contractPackageRegex>
                            <baseClassFQN>com.vasquez.BeerMessagingBase</baseClassFQN>
                        </baseClassMapping>
                    </baseClassMappings>
                    <!-- We want to use the JAR with contracts with the following coordinates -->
                    <contractDependency>
                        <groupId>com.vasquez</groupId>
                        <artifactId>beer-contracts</artifactId>
                    </contractDependency>
                    <!-- Base package for generated tests -->
                    <basePackageForTests>com.vasquez</basePackageForTests>

                    <!-- The JAR with contracts should be taken from Maven local -->
                    <contractsMode>LOCAL</contractsMode>
                    <!-- The JAR with contracts will get downloaded from an external repo -->
<!--                    <contractsRepositoryUrl>https://foo.bar/baz</contractsRepositoryUrl>-->

                </configuration>
            </plugin>
```

When the producer project is built the Spring Cloud Contract Verifier generates test classes like the one below based on the contract JAR.

```java
public class Beer_api_consumerTest extends BeerMessagingBase {
	@Inject ContractVerifierMessaging contractVerifierMessaging;
	@Inject ContractVerifierObjectMapper contractVerifierObjectMapper;

	@Test
	public void validate_shouldSendAcceptedVerification() throws Exception {
		// when:
			clientIsOldEnough();

		// then:
			ContractVerifierMessage response = contractVerifierMessaging.receive("verifications");
			assertThat(response).isNotNull();

		// and:
			assertThat(response.getPayloadAsByteArray()).isEqualTo(fileToBytes(this, "shouldSendAcceptedVerification_response_response-old-enough.bin"));
	}
}
```

Notice the generated test extends from class `BeerMessagingBase`. This is the class specified in the POM file 
and the producer's developers are in charge of implementing that class. Until then the code won't compile. 
The test is also expecting the method `clientIsOldEnough()` to be available in the base class. 
That method name was defined in the contract. Also, as the test runs it verifies that a message was produced 
and it compares that message's payload with the one in the binary file specified in the contract: 
`shouldSendAcceptedVerification_response_response-old-enough.bin`

The test base class should implement methods that trigger the functionality that ends up in producing 
the expected messages to be sent. In this case the `personCheckingService` is the one being tested here.

```java
@AutoConfigureMessageVerifier
public abstract class BeerMessagingBase {

    @Inject
    MessageVerifier messaging;

    @Autowired
    PersonCheckingService personCheckingService;

    public void clientIsOldEnough() {
        personCheckingService.shouldGetBeer(45);
    }

    public void clientIsTooYoung() {
        personCheckingService.shouldGetBeer(9);
    }
}
```

Run command below from the producer folder to run producer tests: 

```shell script
$ ./mvnw test
```
