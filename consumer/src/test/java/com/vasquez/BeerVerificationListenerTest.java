package com.vasquez;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "com.vasquez:beer-api-producer-external")
public class BeerVerificationListenerTest {

    @Autowired
    StubTrigger stubTrigger;

    @Autowired
    BeerVerificationServiceMock beerVerificationServiceMock;

    @BeforeEach
    public void beforeEach() {
        beerVerificationServiceMock.reset();
    }

    @Test
    public void should_call_beer_verification_service_when_verification_was_accepted() {

        // act
        this.stubTrigger.trigger("accepted_verification");

        // assert
        BeerVerification verification = beerVerificationServiceMock.getVerificationInRequest();

        assertThat(verification, is(notNullValue()));

        assertThat(verification.getName(), is(equalTo("santiago vasquez")));
        assertThat(verification.getIsApproved(), is(equalTo(true)));
        assertThat(verification.getBeersCount(), is(equalTo(7)));
        assertThat(verification.getCity(), is(equalTo("medellin")));
        assertThat(verification.getDateOfBirth(), is(equalTo(Instant.parse("1975-04-01T12:00:00Z"))));
    }

    @Test
    public void should_call_beer_verification_service_when_verification_was_rejected() {

        // act
        this.stubTrigger.trigger("rejected_verification");

        // assert
        BeerVerification verification = beerVerificationServiceMock.getVerificationInRequest();

        assertThat(verification, is(notNullValue()));

        assertThat(verification.getName(), is(equalTo("sebastian vasquez")));
        assertThat(verification.getIsApproved(), is(equalTo(false)));
        assertThat(verification.getBeersCount(), is(equalTo(0)));
        assertThat(verification.getCity(), is(equalTo("new york")));
        assertThat(verification.getDateOfBirth(), is(equalTo(Instant.parse("2011-02-15T20:00:00Z"))));
    }
}
