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
public class AbTestingCondition{
    private String experimentId;

    private String bucketKey;


    public String getBucketKey() {
        return (null == bucketKey || bucketKey.trim().length() == 0) ? "" : bucketKey;
    }
}
