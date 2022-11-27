package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class WhiteListConditionMatcherTest {

    @Test
    public void should_match_return_true_when_white_list_is_null() {
        var whiteListCondition = new WhiteListCondition(null);
        var matcher = new WhiteListConditionMatcher(whiteListCondition);

        matcher.match(Customer.builder().customerId(new Customer.CustomerId("123")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_white_list_is_empty() {
        var whiteListCondition = new WhiteListCondition(List.of());
        var matcher = new WhiteListConditionMatcher(whiteListCondition);

        matcher.match(Customer.builder().customerId(new Customer.CustomerId("123")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_false_when_customer_id_not_in_white_list() {
        var whiteListCondition = new WhiteListCondition(List.of("111", "222"));
        var matcher = new WhiteListConditionMatcher(whiteListCondition);

        matcher.match(Customer.builder().customerId(new Customer.CustomerId("333")).build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        matcher.match(Customer.builder().build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_customer_id_in_white_list() {
        var whiteListCondition = new WhiteListCondition(List.of("111", "222"));
        var matcher = new WhiteListConditionMatcher(whiteListCondition);

        matcher.match(Customer.builder().customerId(new Customer.CustomerId("111")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        matcher.match(Customer.builder().customerId(new Customer.CustomerId("222")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

}