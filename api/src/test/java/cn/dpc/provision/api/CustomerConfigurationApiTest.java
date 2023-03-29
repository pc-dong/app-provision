package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.DiffResponse;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static cn.dpc.provision.domain.ConfigurationDescription.StaticStatus.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = CustomerConfigurationApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
class CustomerConfigurationApiTest extends ConfigurationTestBase {

    @Autowired
    WebTestClient webClient;

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

        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/customer-configurations/type").queryParam("customerId", "111").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(DifferContent.class)
                .value(response -> {
                    assertTrue(response.size() == 1);
                    assertTrue(response.get(0).items().get(0).id().equals(publishedRecord.getId().getId()));
                });
    }

    @Test
    public void should_get_diff_success() {
        Configuration publishedRecord = generateConfiguration("type", PUBLISHED);
        ConfigurationDiffer differ = Mockito.mock(ConfigurationDiffer.class);
        when(differFactory.getByType(any())).thenReturn(differ);
        when(differ.diff(eq("FULL_TEST"), any(), any())).thenReturn(Mono.just(new DifferResult(true, false, List.of(), "1111_1")));
        when(differ.diff(eq("INCR_TEST"), any(), any())).thenReturn(Mono.just(new DifferResult(false, false, List.of(), "1111_1")));


        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/customer-configurations/diff")
                        .queryParam("types", "FULL_TEST,INCR_TEST")
                        .queryParam("versions", ",")
                        .queryParam("customerId", "111").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(DiffResponse.class)
                .value(response -> {
                    assertTrue(response.size() == 2);
                });
    }

    @Test
    public void should_get_diff_success_when_differ_throw_error() {
        Configuration publishedRecord = generateConfiguration("type", PUBLISHED);
        ConfigurationDiffer differ = Mockito.mock(ConfigurationDiffer.class);
        when(differFactory.getByType(any())).thenReturn(differ);
        when(differ.diff(eq("FULL_TEST"), any(), any())).thenReturn(Mono.error(new RuntimeException("error")));
        when(differ.diff(eq("INCR_TEST"), any(), any())).thenReturn(Mono.error(new RuntimeException("error")));


        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/customer-configurations/diff")
                        .queryParam("types", "FULL_TEST,INCR_TEST")
                        .queryParam("versions", ",")
                        .queryParam("customerId", "111").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(DiffResponse.class)
                .value(response -> {
                    assertTrue(response.size() == 2);
                });
    }
}