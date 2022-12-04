package cn.dpc.provision.api.config;

import cn.dpc.provision.api.dto.RealTimeRequest;
import io.rsocket.core.Resume;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;

@Configuration
public class ServerConfig {

    public static final MimeType CUSTOMER_MIME = MimeType.valueOf("message/x.customer");
    public static final String CUSTOMER = "customer";

    @Bean
    public RSocketStrategiesCustomizer strategiesCustomizer() {
        return strategies ->
            strategies.metadataExtractorRegistry(registry -> registry.metadataToExtract(MediaType.APPLICATION_JSON, RealTimeRequest.class, CUSTOMER));
    }

    @Bean
    @Primary
    public RSocketMessageHandler messageHandler(RSocketStrategies rSocketStrategies,
                                                ObjectProvider<RSocketMessageHandlerCustomizer> customizers) {
        RSocketMessageHandler messageHandler = new MyRSocketMessageHandler();
        messageHandler.setRSocketStrategies(rSocketStrategies);
        customizers.orderedStream().forEach((customizer) -> customizer.customize(messageHandler));

        return messageHandler;
    }

    @Bean
    public RSocketServerCustomizer serverCustomizer() {
        return rSocketServer -> {
            rSocketServer.resume(new Resume());
//            rSocketServer.lease(leaseSpec -> leaseSpec.maxPendingRequests(256)
//                    .sender(() -> Flux.interval(Duration.ofSeconds(0), Duration.ofMinutes(2))
//                            .map(i -> Lease.create(Duration.ofMinutes(2), 10))));
        };
    }
}
