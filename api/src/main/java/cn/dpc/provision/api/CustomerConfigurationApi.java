package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.DiffRequest;
import cn.dpc.provision.api.dto.DiffResponse;
import cn.dpc.provision.api.dto.RealTimeRequest;
import cn.dpc.provision.domain.CustomerConfigurations;
import cn.dpc.provision.domain.differ.DifferFactory;
import cn.dpc.provision.domain.differ.DifferResult.DifferContent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static cn.dpc.provision.domain.ConfigurationDescription.DynamicStatus.IN_PROGRESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer-configurations")
public class CustomerConfigurationApi {
    private final CustomerConfigurations configurations;
    private final DifferFactory differFactory;

    @GetMapping("/{type}")
    public Flux<DifferContent> getForRealtime(@PathVariable String type,
                                              RealTimeRequest request) {
        return configurations.flux(type, request.parseCustomer())
                .filter(configuration -> configuration.getDescription().getStatus() == IN_PROGRESS)
                .collectList()
                .flatMapIterable(DifferContent::from);
    }

    @GetMapping("/diff")
    public Flux<DiffResponse> diff(DiffRequest request) {
        return Flux.fromIterable(request.getTypeVersionList())
                .flatMap(typeVersion -> Optional.ofNullable(differFactory.getByType(typeVersion.type()))
                        .map(differ -> differ.diff(typeVersion.type(), typeVersion.version(), request.parseCustomer())
                                .map(differResult -> new DiffResponse(typeVersion.type(), differResult))
                                .onErrorReturn(new DiffResponse(typeVersion.type(), null)))
                        .orElse(Mono.just(new DiffResponse(typeVersion.type(), null)))
                );
    }

}
