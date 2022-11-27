package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class AbTestingConditionMatcher implements Matcher{

    private final AbTestingCondition abTestingCondition;
    private final CustomerABAssignments customerABAssignments;

    public Mono<Boolean> match(Customer customer) {
        return Optional.ofNullable(abTestingCondition).map(AbTestingCondition::getExperimentId)
                .map(exId -> exId.trim().length() == 0 ? Mono.just(true)
                        : customerABAssignments.getAssignmentOfExperiment(exId, customer)
                        .map(assignment -> bucketKeyEquals(assignment.getBucketKey()))
                        .onErrorReturn(false)
                )
                .orElse(Mono.just(true));
    }

    private boolean bucketKeyEquals(String resultBucketKey) {
        return Objects.equals(Optional.ofNullable(abTestingCondition).map(AbTestingCondition::getBucketKey).orElse(""),
                (null == resultBucketKey || resultBucketKey.trim().length() == 0) ? "" : resultBucketKey);
    }
}
