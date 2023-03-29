package cn.dpc.provision.api;

import cn.dpc.provision.api.config.annotation.GetMessageMapping;
import cn.dpc.provision.api.dto.DiffRSocketRequest;
import cn.dpc.provision.api.dto.DiffResponse;
import cn.dpc.provision.api.dto.RealTimeRequest;
import cn.dpc.provision.domain.CustomerConfigurations;
import cn.dpc.provision.domain.differ.DifferFactory;
import cn.dpc.provision.domain.differ.DifferResult.DifferContent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static cn.dpc.provision.api.config.ServerConfig.CUSTOMER;
import static cn.dpc.provision.domain.ConfigurationDescription.DynamicStatus.IN_PROGRESS;

@Controller
@RequiredArgsConstructor
@MessageMapping("customer-configurations")
public class CustomerConfigurationRSocketApi {
    private final CustomerConfigurations configurations;
    private final DifferFactory differFactory;

    @GetMessageMapping("{type}")
    public Flux<DifferContent> getForRealtime(@DestinationVariable String type,
                                              @Payload RealTimeRequest request) {
        return configurations.flux(type, request.parseCustomer())
                .filter(configuration -> configuration.getDescription().getStatus() == IN_PROGRESS)
                .collectList()
                .flatMapIterable(DifferContent::from);
    }

    @MessageMapping("diff")
    public Flux<DiffResponse> diff(@Header(CUSTOMER) RealTimeRequest customer,
                                   @Payload Flux<DiffRSocketRequest> request) {
        return request
                .flatMap(typeVersion -> Optional.ofNullable(differFactory.getByType(typeVersion.getType()))
                        .map(differ -> differ.diff(typeVersion.getType(), typeVersion.getVersion(), customer.parseCustomer())
                                .map(differResult -> new DiffResponse(typeVersion.getType(), differResult))
                                .onErrorReturn(new DiffResponse(typeVersion.getType(), null)))
                        .orElse(Mono.just(new DiffResponse(typeVersion.getType(), null)))
                );
    }

}
