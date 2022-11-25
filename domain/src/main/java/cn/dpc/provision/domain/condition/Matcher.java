package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import reactor.core.publisher.Mono;

public interface Matcher {
    Mono<Boolean> match(Customer customer);
}
