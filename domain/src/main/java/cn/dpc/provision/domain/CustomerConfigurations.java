package cn.dpc.provision.domain;

import reactor.core.publisher.Flux;

import java.util.function.Predicate;

public interface CustomerConfigurations {
    Flux<Configuration> flux(String type, Customer customer);
}
