org.springframework.cloud.contract.spec.Contract.make {
    description("""
Sends a positive verification message when person is eligible to get a beer

```
given:
    client is old enough
when:
    he applies for a beer
then:
    we'll send a message with a positive verification
```  
""")
    // label by means of which the output message can be triggered
    label 'accepted_verification'

    // even when there is not data here, it must be included empty to avoid null pointer exception when generating tests
    input {}

    // output message of the contract
    outputMessage {
        // destination to which the output message will be sent to
        sentTo 'verifications'

        // the body of the output message
        body(file("temp/encoded-messages/response-old-enough.bin.base64"))
    }
}
