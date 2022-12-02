package cn.dpc.provision.persistence.repository;

import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ConfigurationDBRepository extends ReactiveCrudRepository<ConfigurationDB, String> {

    Flux<ConfigurationDB> findByType(String type, Sort sort);
}
