package cn.dpc.provision.domain;

import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeatureFlags {
    Flux<FeatureFlag> findAll();

    Mono<FeatureFlag> getByFeatureKey(String featureKey);

    Mono<FeatureFlag> add(FeatureFlag featureFlag);

    Mono<FeatureFlag> update(String featureKey, FeatureFlagDescription description);

    Mono<Void> publish(String featureKey);

    Mono<Void> disable(String featureKey);

    Mono<Void> delete(String featureKey);
}
