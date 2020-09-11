package com.vasquez;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProducerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class BeerMessagingBase {

    @Autowired
    PersonCheckingService personCheckingService;

    public void clientIsOldEnough() {
        personCheckingService.shouldGetBeer(45);
    }

    public void clientIsTooYoung() {
        personCheckingService.shouldGetBeer(9);
    }
}
