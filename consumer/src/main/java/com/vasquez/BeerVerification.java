package com.vasquez;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class BeerVerification {
    private final String name;
    private final Boolean isApproved;
    private final int beersCount;
    private final String city;
    private final Instant dateOfBirth;
}
