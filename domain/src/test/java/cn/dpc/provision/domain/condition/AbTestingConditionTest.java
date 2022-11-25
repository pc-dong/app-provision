package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.test.StepVerifier;

class AbTestingConditionTest {

    @Mock
    CustomerABAssignments customerABAssignments;

     @Test
    public void should_match_return_true_when_experiment_id_is_null() {
        var abTestingCondition = new AbTestingCondition(null, null, null);

        abTestingCondition.match(Customer.builder()
                .customerId(new Customer.CustomerId("111"))
                        .adCode("111111")
                        .customerLevel(Customer.CustomerLevel.NEW_USER).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }


}