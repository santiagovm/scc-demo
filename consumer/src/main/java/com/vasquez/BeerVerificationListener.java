package com.vasquez;

import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class BeerVerificationListener {

    private final BeerVerificationService _beerVerificationService;

    @StreamListener(Sink.INPUT)
    public void listen(byte[] rawMessage) {

        // todo: fake implementation
        BeerVerification fakeVerification = BeerVerification.builder().build();
        _beerVerificationService.process(fakeVerification);

    }

}
