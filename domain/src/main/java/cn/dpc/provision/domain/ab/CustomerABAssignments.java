package cn.dpc.provision.domain.ab;

import cn.dpc.provision.domain.Customer;
import reactor.core.publisher.Mono;

public interface CustomerABAssignments {
    Mono<Assignment> getAssignmentOfExperiment(String experimentId, Customer customer);
}
