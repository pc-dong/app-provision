package cn.dpc.provision.domain.condition;

import cn.dpc.provision.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static cn.dpc.provision.domain.Customer.ANONYMOUS_CUSTOMER;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCriteriaCondition {
    private WhiteListCondition whiteListCondition;

    private LocationCondition locationCondition;

    private AbTestingCondition abTestingCondition;
}
