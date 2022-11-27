package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class LocationConditionMatcherTest {

    @Test
    public void should_return_true_when_adCodes_is_null() {
        var locationCondition = new LocationCondition(null);
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("112233").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("112200").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("110000").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode(null).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_return_true_when_adCodes_is_empty() {
        var locationCondition = new LocationCondition(List.of());
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("112233").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("112200").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("110000").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode(null).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_return_true_when_adCodes_match_province() {
        var locationCondition = new LocationCondition(List.of("600000", "500000"));
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("602233").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("602200").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("600000").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_return_false_when_adCode_not_match_province() {
        var locationCondition = new LocationCondition(List.of("500000", "700000"));
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("602233").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("602200").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("600000").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void should_return_true_when_adCodes_match_city() {
        var locationCondition = new LocationCondition(List.of("600100", "500100"));
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("600133").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("600100").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_return_false_when_adCode_not_match_city() {
        var locationCondition = new LocationCondition(List.of("500100", "700100"));
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("500233").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("500200").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

    }

    @Test
    public void should_return_true_when_adCodes_match_area() {
        var locationCondition = new LocationCondition(List.of("600102", "500102"));
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("600102").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("500102").build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_return_false_when_adCode_not_match_area() {
        var locationCondition = new LocationCondition(List.of("500102", "700102"));
        var matcher = new LocationConditionMatcher(locationCondition);

        matcher.match(Customer.builder().adCode("500103").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("500203").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        matcher.match(Customer.builder().adCode("600100").build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }
}