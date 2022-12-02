package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.CustomerCriteriaResults;
import cn.dpc.provision.domain.ab.Assignment;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import cn.dpc.provision.domain.condition.AbTestingCondition;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import cn.dpc.provision.domain.condition.LocationCondition;
import cn.dpc.provision.domain.condition.WhiteListCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerCriteriaResultsImplTest {

    @Mock
    CustomerABAssignments abAssignments;

    CustomerCriteriaResults customerCriteriaResults;

    @BeforeEach
    public void setUp() {
        customerCriteriaResults = new CustomerCriteriaResultsImpl(abAssignments);
    }

    @Test
    public void should_get_result_success() {
        Configuration configuration = new Configuration(new Configuration.ConfigurationId("111"), "type", ConfigurationDescription.builder().build(),
                new CustomerCriteriaCondition(new WhiteListCondition(List.of("111", "222")), new LocationCondition(List.of("110908")),
                new AbTestingCondition("experiment1", "bucketKey1")));
        Customer customer = new Customer(new Customer.CustomerId("111"), "110908", Customer.CustomerLevel.VIP);

        when(abAssignments.getAssignmentOfExperiment(anyString(), any())).thenReturn(Mono.just(new Assignment("experiment1", "bucketKey1")));

        customerCriteriaResults.getResult(configuration, customer)
                .as(StepVerifier::create)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void should_get_result_false_when_condition_not_match() {
        Configuration configuration = new Configuration(new Configuration.ConfigurationId("111"), "type", ConfigurationDescription.builder().build(),
                new CustomerCriteriaCondition(new WhiteListCondition(List.of("111", "222")), new LocationCondition(List.of("110908")),
                        new AbTestingCondition("experiment1", "bucketKey1")));
        Customer customer = new Customer(new Customer.CustomerId("333"), "110908", Customer.CustomerLevel.VIP);

        customerCriteriaResults.getResult(configuration, customer)
                .as(StepVerifier::create)
                .expectNext(false)
                .verifyComplete();
    }
}