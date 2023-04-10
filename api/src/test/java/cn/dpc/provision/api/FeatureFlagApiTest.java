package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.FeatureFlagRequest;
import cn.dpc.provision.api.dto.FeatureFlagResponse;
import cn.dpc.provision.api.dto.PageResponse;
import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlags;
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

import java.util.List;

import static cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription.DataType.BOOLEAN;
import static cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription.Status.PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = FeatureFlagApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
class FeatureFlagApiTest {
    @Autowired
    WebTestClient webClient;

    @MockBean
    FeatureFlags featureFlags;

    @Test
    public void should_add_new_feature_flag_success() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = generateFeatureFlag(featureKey);
        when(featureFlags.add(any(FeatureFlag.class))).thenReturn(Mono.just(featureFlag));
        webClient.post()
                .uri("/feature-flags")
                .body(Mono.just(generateFeatureRequest(featureFlag)), FeatureFlagRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FeatureFlagResponse.class)
                .value(response -> {
                    assertTrue(response.getId().length() > 0);
                    assertEquals(response.getDescription().status(), PUBLISHED);
                });
    }

    @Test
    public void should_add_new_feature_flag_throw_exception_when_feature_key_is_blank() {
        String featureKey = "";
        FeatureFlag featureFlag = generateFeatureFlag(featureKey);
        FeatureFlagRequest request = generateFeatureRequest(featureFlag);
        request.setFeatureKey("");

        webClient.post()
                .uri("/feature-flags")
                .body(Mono.just(request), FeatureFlagRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void should_list_all_feature_flags_return_empty_list_when_not_feature_flag_found() {
        when(featureFlags.findAll()).thenReturn(Flux.empty());
        webClient.get()
                .uri("/feature-flags")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(FeatureFlagResponse.class)
                .hasSize(0);
    }

    @Test
    public void should_list_all_feature_flags_return_list_of_feature_flags() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = generateFeatureFlag(featureKey);
        when(featureFlags.findAll()).thenReturn(Flux.just(featureFlag));
        webClient.get()
                .uri("/feature-flags")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(FeatureFlagResponse.class)
                .hasSize(1)
                .value(response -> {
                    assertTrue(response.get(0).getId().length() > 0);
                    assertEquals(response.get(0).getDescription().status(), PUBLISHED);
                });
    }

    @Test
    public void should_list_all_with_page_return_by_page_feature_flags() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = generateFeatureFlag(featureKey);
        when(featureFlags.listByPage(anyInt(), anyInt(), anyString())).thenReturn(Flux.just(featureFlag));
        when(featureFlags.countAll(anyString())).thenReturn(Mono.just(2L));

        webClient.get()
                .uri("/feature-flags/page?page=0&pageSize=1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PageResponse.class)
                .value(response -> {
                    assertEquals(1, response.getContent().size());
                    assertEquals(2, response.getTotal());
                });
    }

    @Test
    public void should_get_by_id_success() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = generateFeatureFlag(featureKey);
        when(featureFlags.getByFeatureKey(any())).thenReturn(Mono.just(featureFlag));

        webClient.get()
                .uri("/feature-flags/" + featureKey)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(FeatureFlagResponse.class)
                .value(response -> assertEquals(response.getFeatureKey(), featureKey));
    }

    private FeatureFlagRequest generateFeatureRequest(FeatureFlag featureFlag) {
        FeatureFlagRequest request =  new FeatureFlagRequest();
        request.setFeatureKey(featureFlag.getFeatureKey());
        request.setDescription(featureFlag.getDescription());
        return request;
    }

    private FeatureFlag generateFeatureFlag(String featureKey) {
        return new FeatureFlag(featureKey, new FeatureFlag.FeatureFlagDescription("name",
                "description",
                BOOLEAN,
                "true",
                PUBLISHED,
                new FeatureFlag.FeatureConfigTemplate(List.of(
                        new FeatureFlag.FeatureConfigTemplate.Item("key",
                                "name",
                                "description",
                                FeatureFlag.FeatureConfigTemplate.DataType.BOOLEAN,
                                "false",
                                null)))
        ));
    }


}