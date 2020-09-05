package com.vasquez;

import lombok.Data;

import java.time.Instant;

@Data
public class BeerVerification {
    private final String name;
    private final Boolean isApproved;
    private final int beersCount;
    private final String city;
    private final Instant dateOfBirth;

    public BeerVerification(Beer.Response responseProto) {
        name = responseProto.getName();
        isApproved = responseProto.getStatus() == Beer.Response.BeerCheckStatus.OK;
        beersCount = responseProto.getBeersCount();
        city = responseProto.getCity();
        dateOfBirth = Instant.ofEpochSecond(responseProto.getDob());
    }
}
