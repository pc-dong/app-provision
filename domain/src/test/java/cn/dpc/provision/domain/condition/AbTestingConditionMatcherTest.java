package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.ab.Assignment;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbTestingConditionMatcherTest {

    @Mock
    CustomerABAssignments customerABAssignments;

    @Test
    public void should_match_return_true_when_experiment_id_is_null() {
        var abTestingCondition = new AbTestingCondition(null, null);
        var matcher = new AbTestingConditionMatcher(abTestingCondition, customerABAssignments);

        matcher.match(Customer.builder()
                        .customerId(new Customer.CustomerId("111"))
                        .adCode("111111")
                        .customerLevel(Customer.CustomerLevel.NEW_USER).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_experiment_id_and_bucket_match() {
        String experimentId = "12123";
        String bucketKey = "122";
        var abTestingCondition = new AbTestingCondition(experimentId, bucketKey);
        var matcher = new AbTestingConditionMatcher(abTestingCondition, customerABAssignments);
        when(customerABAssignments.getAssignmentOfExperiment(any(), any()))
                .thenReturn(Mono.just(new Assignment(experimentId, bucketKey)));

        matcher.match(Customer.builder()
                        .customerId(new Customer.CustomerId("111"))
                        .adCode("111111")
                        .customerLevel(Customer.CustomerLevel.NEW_USER).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_true_when_experiment_id_and_bucket_match_for_bucket_is_null() {
        String experimentId = "12123";
        String bucketKey = null;
        var abTestingCondition = new AbTestingCondition(experimentId, bucketKey);
        var matcher = new AbTestingConditionMatcher(abTestingCondition, customerABAssignments);
        when(customerABAssignments.getAssignmentOfExperiment(any(), any()))
                .thenReturn(Mono.just(new Assignment(experimentId, "")));

        matcher.match(Customer.builder()
                        .customerId(new Customer.CustomerId("111"))
                        .adCode("111111")
                        .customerLevel(Customer.CustomerLevel.NEW_USER).build())
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_match_return_false_when_experiment_id_and_bucket_not_match() {
        String experimentId = "12123";
        String bucketKey = "122";
        var abTestingCondition = new AbTestingCondition(experimentId, bucketKey);
        var matcher = new AbTestingConditionMatcher(abTestingCondition, customerABAssignments);
        when(customerABAssignments.getAssignmentOfExperiment(any(), any()))
                .thenReturn(Mono.just(new Assignment(experimentId, "bucketKey")));

        matcher.match(Customer.builder()
                        .customerId(new Customer.CustomerId("111"))
                        .adCode("111111")
                        .customerLevel(Customer.CustomerLevel.NEW_USER).build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void should_match_return_false_when_getAssignmentOfExperiment_throw_error() {
        String experimentId = "12123";
        String bucketKey = "122";
        var abTestingCondition = new AbTestingCondition(experimentId, bucketKey);
        var matcher = new AbTestingConditionMatcher(abTestingCondition, customerABAssignments);
        when(customerABAssignments.getAssignmentOfExperiment(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("error")));

        matcher.match(Customer.builder()
                        .customerId(new Customer.CustomerId("111"))
                        .adCode("111111")
                        .customerLevel(Customer.CustomerLevel.NEW_USER).build())
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }
}