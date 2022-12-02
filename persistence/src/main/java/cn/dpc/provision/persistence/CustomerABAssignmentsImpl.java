package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.ab.Assignment;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerABAssignmentsImpl implements CustomerABAssignments {
    @Override
    public Mono<Assignment> getAssignmentOfExperiment(String experimentId, Customer customer) {
        // TODO: Call AB Testing assignments API
        return Mono.just(new Assignment(experimentId, "bucketKey"));
    }
}
