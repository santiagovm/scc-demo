package com.vasquez;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

// santi: verify all annotations are needed
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProducerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class BeerMessagingBase {

    @Inject
    MessageVerifier messaging;

    @Before // santti: is this really needed?
    public void before() {
        // let's clear any remaining messages
        // output == destination or channel name
        messaging.receive("output", 100, TimeUnit.MILLISECONDS);
    }

    @Autowired
    PersonCheckingService personCheckingService;

    public void clientIsOldEnough() {
        personCheckingService.shouldGetBeer(45);
    }

    public void clientIsTooYoung() {
        personCheckingService.shouldGetBeer(9);
    }
}
