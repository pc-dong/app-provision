package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WhiteListCondition implements Matcher{
    private List<String> whiteList;

    private boolean match(Customer.CustomerId customerId) {
        return Optional.ofNullable(whiteList)
                .map(list  -> list.isEmpty() || null != customerId && list.contains(customerId.getId()))
                .orElse(true);
    }

    @Override
    public Mono<Boolean> match(Customer customer) {
        return Mono.just(this.match(customer.getCustomerId()));
    }
}
