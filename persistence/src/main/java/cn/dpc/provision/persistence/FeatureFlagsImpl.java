package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import cn.dpc.provision.domain.FeatureFlags;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import cn.dpc.provision.persistence.repository.FeatureFlagDB;
import cn.dpc.provision.persistence.repository.FeatureFlagDBRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription.Status.DELETED;
import static cn.dpc.provision.domain.FeatureFlag.FeatureFlagId.fromFeatureKey;

@Component
public class FeatureFlagsImpl implements FeatureFlags {
    private final FeatureFlagDBRepository featureFlagDBRepository;

    public FeatureFlagsImpl(FeatureFlagDBRepository featureFlagDBRepository) {
        this.featureFlagDBRepository = featureFlagDBRepository;
    }

    @Override
    public Flux<FeatureFlag> findAll() {
        return featureFlagDBRepository.findAll()
                .map(FeatureFlagDB::to)
                .filter(featureFlag -> featureFlag.getDescription().status() != DELETED);
    }

    @Override
    public Flux<FeatureFlag> listByPage(int page, int pageSize, String featureKey) {
        Pageable withPage = Pageable.ofSize(pageSize).withPage(page);
        return featureFlagDBRepository.findAllWithPage(withPage.getOffset(), withPage.getPageSize(), featureKey)
                .map(FeatureFlagDB::to);
    }

    @Override
    public Mono<Long> countAll(String featureKey) {
        return featureFlagDBRepository.countNotDeleted(featureKey);
    }

    @Override
    public Mono<FeatureFlag> getByFeatureKey(String featureKey) {
        return featureFlagDBRepository.findById(fromFeatureKey(featureKey).getId())
                .map(FeatureFlagDB::to)
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(featureKey)));
    }

    @Override
    public Mono<FeatureFlag> add(FeatureFlag featureFlag) {
        return featureFlagDBRepository.save(FeatureFlagDB.from(featureFlag))
                .map(FeatureFlagDB::to);
    }

    @Override
    public Mono<FeatureFlag> update(String featureKey, FeatureFlagDescription description) {
        return featureFlagDBRepository.findById(fromFeatureKey(featureKey).getId())
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(featureKey)))
                .map(featureFlagDB -> featureFlagDB.update(description))
                .flatMap(featureFlagDBRepository::save)
                .map(FeatureFlagDB::to);
    }

    @Override
    public Mono<Void> publish(String featureKey) {
        return featureFlagDBRepository.findById(fromFeatureKey(featureKey).getId())
                .map(FeatureFlagDB::publish)
                .flatMap(featureFlagDBRepository::save)
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(featureKey)))
                .then();
    }

    @Override
    public Mono<Void> disable(String featureKey) {
        return featureFlagDBRepository.findById(fromFeatureKey(featureKey).getId())
                .map(FeatureFlagDB::disable)
                .flatMap(featureFlagDBRepository::save)
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(featureKey)))
                .then();
    }

    @Override
    public Mono<Void> delete(String featureKey) {
        return featureFlagDBRepository.findById(fromFeatureKey(featureKey).getId())
                .map(FeatureFlagDB::delete)
                .flatMap(featureFlagDBRepository::save)
                .then();
    }
}
