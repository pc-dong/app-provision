package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.CustomerCriteriaResults;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import cn.dpc.provision.domain.condition.AbTestingConditionMatcher;
import cn.dpc.provision.domain.condition.CustomerCriteriaConditionMatcher;
import cn.dpc.provision.domain.condition.LocationConditionMatcher;
import cn.dpc.provision.domain.condition.WhiteListConditionMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerCriteriaResultsImpl implements CustomerCriteriaResults {
    private final CustomerABAssignments abAssignments;

    @Override
    public Mono<Boolean> getResult(Configuration configuration, Customer customer) {
        return null == configuration || null == configuration.getCustomerCriteriaCondition() ? Mono.just(true) :
                new CustomerCriteriaConditionMatcher(new WhiteListConditionMatcher(configuration.getCustomerCriteriaCondition().getWhiteListCondition()),
                        new LocationConditionMatcher(configuration.getCustomerCriteriaCondition().getLocationCondition()),
                        new AbTestingConditionMatcher(configuration.getCustomerCriteriaCondition().getAbTestingCondition(), abAssignments))
                        .match(customer);
    }
}
