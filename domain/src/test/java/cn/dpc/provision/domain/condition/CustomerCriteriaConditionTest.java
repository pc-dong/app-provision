package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerCriteriaConditionTest {
    @Mock
    WhiteListCondition whiteListCondition;

    @Mock
    LocationCondition locationCondition;

    @Mock
    AbTestingCondition abTestingCondition;

    @Test
    public void should_match_return_false_when_any_condition_match() {
        when(whiteListCondition.match(any())).thenReturn(Mono.just(true));
        when(locationCondition.match(any())).thenReturn(Mono.just(false));
        var condition = new CustomerCriteriaCondition(whiteListCondition, locationCondition, abTestingCondition);

        condition.match(new Customer(new Customer.CustomerId("111"), "111111", Customer.CustomerLevel.VIP))
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_all_condition_match() {
        when(whiteListCondition.match(any())).thenReturn(Mono.just(true));
        when(locationCondition.match(any())).thenReturn(Mono.just(true));
        when(abTestingCondition.match(any())).thenReturn(Mono.just(true));
        var condition = new CustomerCriteriaCondition(whiteListCondition, locationCondition, abTestingCondition);

        condition.match(new Customer(new Customer.CustomerId("111"), "111111", Customer.CustomerLevel.VIP))
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }


}