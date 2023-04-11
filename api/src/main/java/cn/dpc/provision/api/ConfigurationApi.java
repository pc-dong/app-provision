package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.ConfigurationRequest;
import cn.dpc.provision.api.dto.ConfigurationResponse;
import cn.dpc.provision.api.dto.PageResponse;
import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.Configurations;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("configurations")
@RequiredArgsConstructor
@RestController
public class ConfigurationApi {
    private final Configurations configurations;

    @PostMapping
    Mono<ConfigurationResponse> addNewRecord(@Validated @RequestBody ConfigurationRequest request) {
        return configurations.add(request.toConfiguration())
                .map(ConfigurationResponse::from);
    }

    @PutMapping("/{id}")
    Mono<ConfigurationResponse> updateRecord(@PathVariable String id,
                                             @Validated @RequestBody ConfigurationRequest request) {
        return configurations.update(new Configuration.ConfigurationId(id), request.toConfiguration())
                .map(ConfigurationResponse::from);
    }

    @DeleteMapping("/{id}")
    Mono<Void> updateRecord(@PathVariable String id) {
        return configurations.delete(new Configuration.ConfigurationId(id))
                .then();
    }

    @GetMapping
    Flux<ConfigurationResponse> listAll(String type) {
        return configurations.findAllAvailableByType(type)
                .map(ConfigurationResponse::from);
    }

    @GetMapping("/page")
    Mono<PageResponse<ConfigurationResponse>> listAll(@RequestParam(required = false, defaultValue = "0") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false, defaultValue = "") String key,
                                                      String type) {
        return configurations.findAllByPage(type, page, pageSize, key)
                .map(ConfigurationResponse::from)
                .collectList().zipWith(configurations.countAll(type, key))
                .map(tuple -> PageResponse.<ConfigurationResponse>builder()
                        .content(tuple.getT1())
                        .total(tuple.getT2())
                        .page(page)
                        .pageSize(pageSize)
                        .build());
    }

    @GetMapping("/{id}")
    Mono<ConfigurationResponse> getById(@PathVariable String id) {
        return configurations.getById(new Configuration.ConfigurationId(id))
                .map(ConfigurationResponse::from);
    }

    @PutMapping("/{id}/publish")
    Mono<Void> publish(@PathVariable String id) {
        return configurations.publish(new Configuration.ConfigurationId(id));
    }

    @PutMapping("/{id}/disable")
    Mono<Void> disable(@PathVariable String id) {
        return configurations.disable(new Configuration.ConfigurationId(id));
    }

    @GetMapping("/{type}/available")
    Flux<ConfigurationResponse> getAvailableList(@PathVariable String type) {
        return configurations.findAllAvailableByType(type)
                .map(ConfigurationResponse::from);
    }

    @PutMapping("/priority")
    Mono<Void> priority(@RequestBody List<String> ids) {
        return configurations.priority(ids.stream()
                .map(Configuration.ConfigurationId::new).collect(Collectors.toList()));
    }

}
