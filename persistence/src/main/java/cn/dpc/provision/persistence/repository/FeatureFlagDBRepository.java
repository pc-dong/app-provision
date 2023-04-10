package cn.dpc.provision.persistence.repository;

import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeatureFlagDBRepository extends ReactiveSortingRepository<FeatureFlagDB, String> {
    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND (#{#featureKey==''}  OR featureKey LIKE '%#{#featureKey}%') AND description.status != 'DELETED' offset #{#offset} limit #{#limit} ")
    Flux<FeatureFlagDB> findAllWithPage(@Param("offset") long offset, @Param("limit") int limit, @Param("featureKey") String featureKey);

    @Query("SELECT COUNT(1) from #{#n1ql.bucket} WHERE #{#n1ql.filter} AND (#{#featureKey==''}  OR featureKey LIKE '%#{#featureKey}%' ) AND description.status != 'DELETED' ")
    Mono<Long> countNotDeleted(@Param("featureKey") String featureKey);
}
