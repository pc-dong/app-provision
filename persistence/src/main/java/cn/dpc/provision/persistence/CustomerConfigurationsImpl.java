package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.Customer;
import cn.dpc.provision.domain.CustomerConfigurations;
import cn.dpc.provision.domain.CustomerCriteriaResults;
import cn.dpc.provision.persistence.repository.ConfigurationDB;
import cn.dpc.provision.persistence.repository.ConfigurationDBRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CustomerConfigurationsImpl implements CustomerConfigurations {
    private final ConfigurationDBRepository repository;
    private final CustomerCriteriaResults customerCriteriaResults;

    static final Cache<String, Flux<ConfigurationDB>> configurationCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    @Override
    public Flux<Configuration> flux(String type, Customer customer) {
        return loadConfigurationWithCache(type)
                .map(ConfigurationDB::to)
                .filterWhen(configuration -> customerCriteriaResults.getResult(configuration, customer));
    }

    private Flux<ConfigurationDB> loadConfigurationWithCache(String type) {
        String key = "Configurations." + type;
        return configurationCache.get(key, code ->
                        repository.findByType(type, Sort.by(Sort.Order.asc("priority"),
                                Sort.Order.desc("description.updatedAt"))).cache())
                .onErrorResume(e -> {
                    configurationCache.invalidate(key);
                    return Mono.error(e);
                });
    }
}
