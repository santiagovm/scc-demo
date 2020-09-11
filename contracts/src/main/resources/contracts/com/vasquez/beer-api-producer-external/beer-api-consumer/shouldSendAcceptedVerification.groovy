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
