package cn.dpc.provision.domain;

import reactor.core.publisher.Flux;

public interface CustomerConfigurations {
    Flux<Configuration> flux(String type, Customer customer);
}
