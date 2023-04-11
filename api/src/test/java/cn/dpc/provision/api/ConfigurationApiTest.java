package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.ConfigurationRequest;
import cn.dpc.provision.api.dto.ConfigurationResponse;
import cn.dpc.provision.api.dto.PageResponse;
import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.Configurations;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = ConfigurationApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
class ConfigurationApiTest extends ConfigurationTestBase{
    @Autowired
    WebTestClient webClient;

    @MockBean
    Configurations configurations;

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
                    assertEquals(response.getStaticStatus(), ConfigurationDescription.StaticStatus.DRAFT);
                });
    }

    @Test
    public void should_add_new_configuration_error_when_type_is_empty() {
        webClient.post()
                .uri("/configurations")
                .body(Mono.just(generateConfigurationRequest("")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(System.out::println);
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
                    assertEquals(response.getStaticStatus(), ConfigurationDescription.StaticStatus.DRAFT);
                });
    }

    @Test
    public void should_update_configuration_error_when_type_is_empty() {
        String id = "id";

        webClient.put()
                .uri("/configurations/" + id)
                .body(Mono.just(generateConfigurationRequest("type")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(System.out::println);
    }

    @Test
    public void should_update_configuration_error_when_id_is_not_exist() {
        String id = "not_exist";
        when(configurations.update(any(), any(Configuration.class))).thenReturn(Mono.error(new ConfigurationNotFoundException(id)));

        webClient.put()
                .uri("/configurations/" + id)
                .body(Mono.just(generateConfigurationRequest("type")), ConfigurationRequest.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void should_findAll_by_page_success() {
        when(configurations.findAllByPage(anyString(), anyInt(), anyInt(), anyString())).thenReturn(Flux.just(generateConfiguration("type")));
        when(configurations.countAll(anyString(), anyString())).thenReturn(Mono.just(2L));

        webClient.get()
                .uri("/configurations/page?type=type&page=1&pageSize=10&key=")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PageResponse.class)
                .value(response -> {
                    assertEquals(response.getTotal(), 2);
                    assertEquals(response.getContent().size(), 1);
                });
    }
}