package com.vasquez;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;

@Slf4j
@AllArgsConstructor
@Component
public class BeerVerificationListener {

    private final BeerVerificationService beerVerificationService;

    @StreamListener(Sink.INPUT)
    public void listen(byte[] rawMessage) {
        Beer.Response responseProto = parseProtoBytes(rawMessage);
        BeerVerification verification = new BeerVerification(responseProto);
        beerVerificationService.process(verification);
    }

    @SneakyThrows
    private Beer.Response parseProtoBytes(byte[] rawMessage) {

        byte[] noQuotes = removeQuotes(rawMessage);
        byte[] noNewLine = removeNewLine(noQuotes);
        byte[] decodedBytes = Base64.getDecoder().decode(noNewLine);

        return Beer.Response.parseFrom(decodedBytes);
    }

    private byte[] removeNewLine(byte[] bytes) {
        return Arrays.copyOfRange(bytes, 0, bytes.length - 2);
    }

    private byte[] removeQuotes(byte[] bytes) {
        return Arrays.copyOfRange(bytes, 1, bytes.length - 1);
    }
}
