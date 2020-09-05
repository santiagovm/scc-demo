org.springframework.cloud.contract.spec.Contract.make {
    description("""
Sends a negative verification message when person is not eligible to get a beer

```
given:
    client is too young
when:
    he applies for a beer
then:
    we'll send a message with a negative verification
```  
""")
    // label by means of which the output message can be triggered
    label 'rejected_verification'

    // even when there is not data here, it must be included empty to avoid null pointer exception when generating tests
    input {}

    // output message of the contract
    outputMessage {
        // destination to which the output message will be sent to
        sentTo 'verifications'

        // the body of the output message
        body(file("temp/encoded-messages/response-too-young.bin.base64"))
    }
}
