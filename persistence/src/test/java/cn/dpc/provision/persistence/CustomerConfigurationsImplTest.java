package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.persistence.repository.ConfigurationDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerConfigurationsImplTest extends ConfigurationTestBase {
    @Mock
    ConfigurationDBRepository repository;

    @Mock
    CustomerCriteriaResults customerCriteriaResults;

    CustomerConfigurations customerConfigurations;

    @BeforeEach
    public void init() {
        customerConfigurations = new CustomerConfigurationsImpl(repository, customerCriteriaResults);
    }

    @Test
    public void should_flux_success() {
        when(repository.findByType(eq("type"), any()))
                .thenReturn(Flux.just(generateConfigurationDB(null, "key", ConfigurationDescription.StaticStatus.PUBLISHED, new ConfigurationDescription.TimeRange()),
                        generateConfigurationDB(null, "key", ConfigurationDescription.StaticStatus.PUBLISHED, new ConfigurationDescription.TimeRange()),
                        generateConfigurationDB(null, "key", ConfigurationDescription.StaticStatus.PUBLISHED, new ConfigurationDescription.TimeRange())));

        when(customerCriteriaResults.getResult(any(), any())).thenReturn(Mono.just(true));

        customerConfigurations.flux("type", new Customer())
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();

        customerConfigurations.flux("type", new Customer())
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();

        verify(repository, times(1)).findByType(eq("type"), any());
    }
}