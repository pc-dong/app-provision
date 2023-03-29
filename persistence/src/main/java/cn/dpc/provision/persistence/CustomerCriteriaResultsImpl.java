package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.AbstractCustomerCriteriaResults;
import cn.dpc.provision.domain.ab.CustomerABAssignments;
import org.springframework.stereotype.Component;

@Component
public class CustomerCriteriaResultsImpl extends AbstractCustomerCriteriaResults {
    public CustomerCriteriaResultsImpl(CustomerABAssignments abAssignments) {
        super(abAssignments);
    }
}
