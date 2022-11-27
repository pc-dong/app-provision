package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.ConfigurationRequest;
import cn.dpc.provision.api.dto.ConfigurationResponse;
import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ConfigurationApi.class)
@ContextConfiguration(classes = TestConfiguration.class)
class ConfigurationApiTest {
    @Autowired
    WebTestClient webClient;

    @MockBean
    Configurations configurations;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_add_new_configuration_success() {
        when(configurations.add(any(Configuration.class))).thenReturn(Mono.just(generateConfiguration("type")));
        webClient.post()
                .uri("/configurations")
                .body(Mono.just(generateConfigurationRequest("type")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ConfigurationResponse.class)
                .value(response -> {
                    assertTrue(response.getId().length() > 0);
                    assertEquals(response.getStaticStatus(), StaticStatus.DRAFT);
                });
    }

    @Test
    public void should_add_new_configuration_error_when_type_is_empty() throws JsonProcessingException {
        webClient.post()
                .uri("/configurations")
                .body(Mono.just(generateConfigurationRequest("")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(value -> System.out.println(value));
    }

    @Test
    public void should_update_configuration_success() {
        String id = "id";
        when(configurations.update(any(), any(Configuration.class))).thenReturn(Mono.just(generateConfiguration("type")));

        webClient.put()
                .uri("/configurations/" + id)
                .body(Mono.just(generateConfigurationRequest("type")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ConfigurationResponse.class)
                .value(response -> {
                    assertTrue(response.getId().length() > 0);
                    assertEquals(response.getStaticStatus(), StaticStatus.DRAFT);
                });
    }

    @Test
    public void should_update_configuration_error_when_type_is_empty() throws JsonProcessingException {
        String id = "id";

        webClient.put()
                .uri("/configurations/" + id)
                .body(Mono.just(generateConfigurationRequest("type")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(value -> System.out.println(value));
    }

    @Test
    public void should_update_configuration_error_when_id_is_not_exist() throws JsonProcessingException {
        String id = "not_exist";
        when(configurations.update(any(), any(Configuration.class))).thenReturn(Mono.error(new ConfigurationNotFoundException(id)));

        webClient.put()
                .uri("/configurations/" + id)
                .body(Mono.just(generateConfigurationRequest("type")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @SneakyThrows
    private ConfigurationRequest generateConfigurationRequest(String type) {
        var request = new ConfigurationRequest();
        request.setType(type);
        request.setKey("TEST_KEY");
        request.setData(objectMapper.readValue("{\"a\": \"b\"}", Object.class));
        request.setTrackingData(objectMapper.readValue("{\"c\": \"d\"}", Object.class));
        request.setTitle("title");
        request.setDescription("description");
        request.setDisplayRule(new DisplayRule());
        request.setTimeRange(new TimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        request.setCustomerCriteriaCondition(new CustomerCriteriaCondition());
        return request;
    }

    @SneakyThrows
    private Configuration generateConfiguration(String type) {
        Configuration configuration = generateConfigurationRequest(type).toConfiguration();
        configuration.setId(new Configuration.ConfigurationId(UUID.randomUUID().toString()));
        return configuration;
    }
}