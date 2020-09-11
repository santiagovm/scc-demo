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

    // method in producer that triggers the output message
    input {
        triggeredBy("clientIsTooYoung()")
    }

    // output message of the contract
    outputMessage {
        // destination to which the output message will be sent to
        sentTo 'verifications'

        // the body of the output message
        body(fileAsBytes("temp/encoded-messages/response-too-young.bin"))
    }
}
