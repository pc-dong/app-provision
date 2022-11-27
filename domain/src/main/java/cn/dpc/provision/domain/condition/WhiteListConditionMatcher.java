package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public class WhiteListConditionMatcher implements Matcher {
    private final WhiteListCondition whiteListCondition;
    private boolean match(Customer.CustomerId customerId) {
        return Optional.ofNullable(whiteListCondition).map(WhiteListCondition::getWhiteList)
                .map(list -> list.isEmpty() || null != customerId && list.contains(customerId.getId()))
                .orElse(true);
    }

    @Override
    public Mono<Boolean> match(Customer customer) {
        return Mono.just(this.match(customer.getCustomerId()));
    }
}
