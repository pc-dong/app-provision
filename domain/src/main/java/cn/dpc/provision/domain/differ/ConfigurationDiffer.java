package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.Customer;
import reactor.core.publisher.Mono;

public interface ConfigurationDiffer {
    Mono<DifferResult> diff(String type, String version, Customer customer);
}
