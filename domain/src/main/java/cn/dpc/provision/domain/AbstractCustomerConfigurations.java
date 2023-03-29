package cn.dpc.provision.domain;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public abstract class AbstractCustomerConfigurations implements CustomerConfigurations {

    private final CustomerCriteriaResults customerCriteriaResults;

    protected abstract Flux<Configuration> loadAllConfiguration(String type);


    public Flux<Configuration> flux(String type, Customer customer) {
        return loadAllConfiguration(type)
                .filterWhen(configuration -> customerCriteriaResults.getResult(configuration, customer));
    }
}
