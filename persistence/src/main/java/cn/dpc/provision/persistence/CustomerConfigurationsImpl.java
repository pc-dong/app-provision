package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.CustomerConfigurations;
import cn.dpc.provision.domain.CustomerCriteriaResults;
import cn.dpc.provision.persistence.repository.ConfigurationDB;
import cn.dpc.provision.persistence.repository.ConfigurationDBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomerConfigurationsImpl implements CustomerConfigurations {
    private final ConfigurationDBRepository repository;
    private final CustomerCriteriaResults customerCriteriaResults;

    @Override
    public Flux<Configuration> flux(String type, Customer customer) {
        return repository.findByType(type, Sort.by(Sort.Order.asc("priority"), Sort.Order.desc("description.updatedAt")))
                .map(ConfigurationDB::to)
                .filterWhen(configuration -> customerCriteriaResults.getResult(configuration, customer));
    }
}
