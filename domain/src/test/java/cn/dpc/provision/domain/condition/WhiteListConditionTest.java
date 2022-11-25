package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.condition.WhiteListCondition;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WhiteListConditionTest {

    @Test
    public void should_match_return_true_when_white_list_is_null() {
        var whiteListCondition = new WhiteListCondition(null);

        whiteListCondition.match(Customer.builder().customerId(new Customer.CustomerId("123")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        whiteListCondition.match(Customer.builder().build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_white_list_is_empty() {
        var whiteListCondition = new WhiteListCondition(List.of());

        whiteListCondition.match(Customer.builder().customerId(new Customer.CustomerId("123")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        whiteListCondition.match(Customer.builder().build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_false_when_customer_id_not_in_white_list() {
        var whiteListCondition = new WhiteListCondition(List.of("111", "222"));

        whiteListCondition.match(Customer.builder().customerId(new Customer.CustomerId("333")).build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();

        whiteListCondition.match(Customer.builder().build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_customer_id_in_white_list() {
        var whiteListCondition = new WhiteListCondition(List.of("111", "222"));

        whiteListCondition.match(Customer.builder().customerId(new Customer.CustomerId("111")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();

        whiteListCondition.match(Customer.builder().customerId(new Customer.CustomerId("222")).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

}