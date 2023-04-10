package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.FeatureFlagRequest;
import cn.dpc.provision.api.dto.FeatureFlagResponse;
import cn.dpc.provision.api.dto.PageResponse;
import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlags;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping("feature-flags")
@RequiredArgsConstructor
public class FeatureFlagApi {
    private final FeatureFlags featureFlags;

    @GetMapping
    public Flux<FeatureFlagResponse> listAll() {
        return featureFlags.findAll().map(FeatureFlagResponse::fromFeatureFlag);
    }

    @GetMapping("page")
    public Mono<PageResponse<FeatureFlagResponse>> listAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                           @RequestParam(required = false, defaultValue = "10") int pageSize){
        return featureFlags.listByPage(page, pageSize)
                .collectList().zipWith(featureFlags.countAll())
                .map(tuple -> PageResponse.<FeatureFlagResponse>builder()
                        .content(tuple.getT1().stream().map(FeatureFlagResponse::fromFeatureFlag).collect(Collectors.toList()))
                        .total(tuple.getT2())
                        .page(page)
                        .pageSize(pageSize)
                        .build());
    }

    @PostMapping
    public Mono<FeatureFlagResponse> addNewRecord(@RequestBody FeatureFlagRequest request) {
        return featureFlags.add(request.toFeatureFlag())
                .map(FeatureFlagResponse::fromFeatureFlag);
    }

    @GetMapping("{featureKey}")
    public Mono<FeatureFlagResponse> getByFeatureKey(@PathVariable String featureKey) {
        return featureFlags.getByFeatureKey(featureKey)
                .map(FeatureFlagResponse::fromFeatureFlag);
    }

    @PutMapping("{featureKey}")
    public Mono<FeatureFlagResponse> updateRecord(@PathVariable String featureKey,
                                                  @RequestBody FeatureFlag.FeatureFlagDescription description) {
        return featureFlags.update(featureKey, description)
                .map(FeatureFlagResponse::fromFeatureFlag);
    }

    @PutMapping("{featureKey}/publish")
    public Mono<Void> publish(@PathVariable String featureKey) {
        return featureFlags.publish(featureKey);
    }

    @PutMapping("{featureKey}/disable")
    public Mono<Void> disable(@PathVariable String featureKey) {
        return featureFlags.disable(featureKey);
    }

    @DeleteMapping("{featureKey}")
    public Mono<Void> delete(@PathVariable String featureKey) {
        return featureFlags.delete(featureKey);
    }
}
