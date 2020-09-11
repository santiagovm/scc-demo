package com.vasquez;

import com.google.protobuf.ByteString;
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

        Response beerResponse = Response.newBuilder()
                .setName("santiago vasquez")
                .setStatus(Response.BeerCheckStatus.OK)
                .setBeersCount(7)
                .setCity("medellin")
                .setDob(Instant.parse("1975-04-01T12:00:00Z").getEpochSecond())
                .build();

        byte[] beerResponseByteArray = beerResponse.toByteArray();

        EventToPublish eventToPublish = EventToPublish.builder()
                .type("foo-event-type")
                .body(beerResponseByteArray)
                .build();

        SomeCustomEnvelope messageEnvelope = SomeCustomEnvelope.newBuilder()
                .setMessageType("foo-message-type")
                .setEventData(ByteString.copyFrom(eventToPublish.getBody()))
                .build();

        Message<byte[]> message = MessageBuilder.withPayload(messageEnvelope.toByteArray())
                .setHeader("contentType", "application/some-custom-mime-type")
                .build();

        this.source.output().send(message);
    }
}
