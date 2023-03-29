package cn.dpc.provision.persistence.repository;

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@N1qlPrimaryIndexed
public interface FeatureFlagDBRepository extends ReactiveCrudRepository<FeatureFlagDB, String> {
}
