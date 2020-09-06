package com.vasquez;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AgeCheckingPersonCheckingService implements PersonCheckingService {

    private final Source source;

    public AgeCheckingPersonCheckingService(Source source) {
        this.source = source;
    }

    @Override
    public void shouldGetBeer(int age) {

        Beer.Response beerResponse = Beer.Response.newBuilder()
                .setName("foo-name")
                .setStatus(Beer.Response.BeerCheckStatus.OK)
                .setBeersCount(99)
                .setCity("foo-city")
                .setDob(Instant.parse("2020-09-06T15:15:15Z").getEpochSecond())
                .build();

        byte[] beerResponseByteArray = beerResponse.toByteArray();
        Message<byte[]> message = MessageBuilder.withPayload(beerResponseByteArray).build();

        this.source.output().send(message);
    }
}
