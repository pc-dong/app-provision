package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static cn.dpc.provision.domain.Customer.ANONYMOUS_CUSTOMER;

@RequiredArgsConstructor
public class CustomerCriteriaConditionMatcher implements Matcher{

    private final WhiteListConditionMatcher whiteListConditionMatcher;
    private final LocationConditionMatcher locationConditionMatcher;
    private final AbTestingConditionMatcher abTestingConditionMatcher;

    public Mono<Boolean> match(Customer customer) {
        var customerInfo = null == customer ? ANONYMOUS_CUSTOMER : customer;

        List<Matcher> matchers = List.of(whiteListConditionMatcher, locationConditionMatcher, abTestingConditionMatcher);
        return Flux.fromIterable(matchers)
                .flatMap(matcher -> matcher.match(customerInfo))
                .all(item -> item);
    }
}
