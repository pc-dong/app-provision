package cn.dpc.provision.domain.condition;


import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AbTestingCondition implements Matcher{
    private String experimentId;

    private String bucketKey;
    private CustomerABAssignments customerABAssignments;

    public String getBucketKey() {
        return (null == bucketKey || bucketKey.trim().length() == 0) ? "" : bucketKey;
    }

    public Mono<Boolean> match(Customer customer) {
        return Optional.ofNullable(experimentId)
                .map(exId -> exId.trim().length() == 0 ? Mono.just(true)
                        : customerABAssignments.getAssignmentOfExperiment(exId, customer)
                        .map(assignment -> bucketKeyEquals(assignment.getBucketKey()))
                )
                .orElse(Mono.just(true));
    }

    private boolean bucketKeyEquals(String resultBucketKey) {
        return Objects.equals(getBucketKey(),
                (null == resultBucketKey || resultBucketKey.trim().length() == 0) ? "" : resultBucketKey);
    }
}
