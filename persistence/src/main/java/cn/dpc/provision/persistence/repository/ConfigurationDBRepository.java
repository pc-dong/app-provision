package cn.dpc.provision.persistence.repository;

import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConfigurationDBRepository extends ReactiveCrudRepository<ConfigurationDB, String> {

    String FILTER_CONDITION = "WHERE #{#n1ql.filter} AND type=$type AND description.staticStatus != 'DELETED' " +
            "AND (#{#key==null || #key==''} OR LOWER(description.title) LIKE '%' || LOWER($key) || '%' OR LOWER(description.description) LIKE '%' || LOWER($key) || '%') ";

    Flux<ConfigurationDB> findByType(String type, Sort sort);

    @Query("#{#n1ql.selectEntity} " + FILTER_CONDITION + "ORDER BY description.updatedAt DESC LIMIT $limit OFFSET $offset")
    Flux<ConfigurationDB> findAllByPage(@Param("type") String type, @Param("key") String key, @Param("offset") long offset, @Param("limit") int limit);


    @Query("SELECT COUNT(1) FROM #{#n1ql.bucket} " + FILTER_CONDITION)
    Mono<Long> countAll(@Param("type") String type, @Param("key") String key);
}
