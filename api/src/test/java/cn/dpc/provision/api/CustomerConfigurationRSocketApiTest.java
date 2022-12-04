package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.DiffRSocketRequest;
import cn.dpc.provision.api.dto.DiffResponse;
import cn.dpc.provision.api.dto.RealTimeRequest;
import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.CustomerConfigurations;
import cn.dpc.provision.domain.differ.ConfigurationDiffer;
import cn.dpc.provision.domain.differ.DifferFactory;
import cn.dpc.provision.domain.differ.DifferResult;
import cn.dpc.provision.domain.differ.DifferResult.DifferContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static cn.dpc.provision.api.ConfigurationTestBase.generateConfiguration;
import static cn.dpc.provision.api.config.ServerConfig.CUSTOMER_MIME;
import static cn.dpc.provision.domain.Customer.CustomerLevel.VIP;
import static cn.dpc.provision.domain.StaticStatus.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


class CustomerConfigurationRSocketApiTest extends RSocketBaseTest {

    @MockBean
    CustomerConfigurations customerConfigurations;

    @MockBean
    DifferFactory differFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_get_by_type_success() {
        Configuration publishedRecord = generateConfiguration("type", PUBLISHED);
        when(customerConfigurations.flux(any(), any())).thenReturn(Flux.just(generateConfiguration("type", DRAFT),
                publishedRecord,
                generateConfiguration("type", DISABLED),
                generateConfiguration("type", DELETED)

        ));

        var request = new RealTimeRequest();
        request.setCustomerId("111");
        requester.route("customer-configurations.type.get")
                .data(Mono.just(request), RealTimeRequest.class)
                .retrieveFlux(DifferContent.class)
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(response ->
                    response.size() == 1 && response.get(0).items().get(0).id().equals(publishedRecord.getId().getId())
                ).verifyComplete();
    }

    @Test
    public void should_get_diff_success() {
        Configuration publishedRecord = generateConfiguration("type", PUBLISHED);
        ConfigurationDiffer differ = Mockito.mock(ConfigurationDiffer.class);
        when(differFactory.getByType(any())).thenReturn(differ);
        when(differ.diff(eq("FULL_TEST"), any(), any())).thenReturn(Mono.just(new DifferResult(true, false, List.of(), "1111_1")));
        when(differ.diff(eq("INCR_TEST"), any(), any())).thenReturn(Mono.just(new DifferResult(false, false, List.of(), "1111_1")));


        var customer = new RealTimeRequest();
        customer.setCustomerId("111");

        requester.route("customer-configurations.diff")
                .metadata(customer, MediaType.APPLICATION_JSON)
                .data(Flux.just(new DiffRSocketRequest("FULL_TEST", null),
                        new DiffRSocketRequest("INCR_TEST", null)), DiffRSocketRequest.class)
                .retrieveFlux(DiffResponse.class)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void should_get_diff_success_when_differ_throw_error() {
        Configuration publishedRecord = generateConfiguration("type", PUBLISHED);
        ConfigurationDiffer differ = Mockito.mock(ConfigurationDiffer.class);
        when(differFactory.getByType(any())).thenReturn(differ);
        when(differ.diff(eq("FULL_TEST"), any(), any())).thenReturn(Mono.error(new RuntimeException("error")));
        when(differ.diff(eq("INCR_TEST"), any(), any())).thenReturn(Mono.error(new RuntimeException("error")));

        var customer = new RealTimeRequest();
        customer.setCustomerId("111");

        requester.route("customer-configurations.diff")
                .metadata(customer, MediaType.APPLICATION_JSON)
                .data(Flux.just(new DiffRSocketRequest("FULL_TEST", null),
                        new DiffRSocketRequest("INCR_TEST", null)))
                .retrieveFlux(DiffResponse.class)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }
}