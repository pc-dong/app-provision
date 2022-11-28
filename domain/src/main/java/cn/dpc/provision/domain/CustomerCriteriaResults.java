package cn.dpc.provision.domain;

import reactor.core.publisher.Mono;

public interface CustomerCriteriaResults {
    Mono<Boolean> getResult(Configuration configuration, Customer customer);
}
