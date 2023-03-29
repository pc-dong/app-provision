package cn.dpc.provision.domain;

import cn.dpc.provision.domain.ab.CustomerABAssignments;
import cn.dpc.provision.domain.condition.AbTestingConditionMatcher;
import cn.dpc.provision.domain.condition.CustomerCriteriaConditionMatcher;
import cn.dpc.provision.domain.condition.LocationConditionMatcher;
import cn.dpc.provision.domain.condition.WhiteListConditionMatcher;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public abstract class AbstractCustomerCriteriaResults implements CustomerCriteriaResults {
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
