package cn.dpc.provision.persistence;

import cn.dpc.provision.TestConfiguration;
import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlags;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import cn.dpc.provision.persistence.repository.FeatureFlagDBRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription.Status.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestConfiguration.class)
@ActiveProfiles("test")
class FeatureFlagsImplTest {
    @Autowired
    FeatureFlagDBRepository repository;

    @Autowired
    FeatureFlags featureFlags;


    @BeforeEach
    public void setUp() {
        repository.deleteAll().then().block(Duration.ofMillis(200));
        Mono.delay(Duration.ofMillis(200)).block();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll().block(Duration.ofMillis(200));
        Mono.delay(Duration.ofMillis(200)).block();
    }

    @Test
    public void should_add_success() {
        String featureKey = "featureKey";

        FeatureFlag featureFlag = getFeatureFlag(featureKey);
        featureFlags.add(featureFlag)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_add_throw_error_when_add_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = getFeatureFlag(featureKey);
        FeatureFlag featureFlag1 = getFeatureFlag(featureKey);

        featureFlags.add(featureFlag).block(Duration.ofSeconds(200));
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.add(featureFlag1)
                .as(StepVerifier::create)
                .expectError(DuplicateKeyException.class)
                .verify();
    }


    @Test
    public void should_update_throw_error_when_not_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = getFeatureFlag(featureKey);

        featureFlags.update(featureKey, featureFlag.getDescription())
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_update_success_when_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag1 = getFeatureFlag(featureKey);

        FeatureFlag featureFlag = getFeatureFlag(featureKey);
        featureFlags.add(featureFlag).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.update(featureKey, featureFlag1.getDescription())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_publish_throw_error_when_not_exist_featureKey() {
        String featureKey = "featureKey";

        featureFlags.publish(featureKey)
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_publish_success_when_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = getFeatureFlag(featureKey);

        featureFlags.add(featureFlag).block();

        featureFlags.publish(featureKey)
                .as(StepVerifier::create)
                .verifyComplete();

        featureFlags.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .expectNextMatches(featureFlag1 -> featureFlag1.getDescription().status() == PUBLISHED)
                .verifyComplete();
    }

    @Test
    public void should_disable_throw_error_when_not_exist_featureKey() {
        String featureKey = "featureKey";

        featureFlags.disable(featureKey)
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }


    @Test
    public void should_disable_success_when_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = getFeatureFlag(featureKey);

        featureFlags.add(featureFlag).block();

        featureFlags.disable(featureKey)
                .as(StepVerifier::create)
                .verifyComplete();

        featureFlags.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .expectNextMatches(featureFlag1 -> featureFlag1.getDescription().status() == DISABLED)
                .verifyComplete();
    }

    @Test
    public void should_getByFeatureKey_throw_error_when_not_exist_featureKey() {
        String featureKey = "featureKey";

        featureFlags.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }


    @Test
    public void should_getByFeatureKey_success_when_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = getFeatureFlag(featureKey);

        featureFlags.add(featureFlag).block();

        featureFlags.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_delete_success_when_not_exist_featureKey() {
        String featureKey = "featureKey";

        featureFlags.delete(featureKey)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_delete_success_when_exist_featureKey() {
        String featureKey = "featureKey";
        FeatureFlag featureFlag = getFeatureFlag(featureKey);

        featureFlags.add(featureFlag).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        featureFlags.delete(featureKey)
                .as(StepVerifier::create)
                .verifyComplete();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.findAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_findAll_return_empty_when_no_records() {
        featureFlags.findAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_findAll_return_data_when_has_records() {

        featureFlags.add(getFeatureFlag("111")).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.add(getFeatureFlag("222")).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.findAll()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void should_listAllByPage_success() {
        featureFlags.add(getFeatureFlag("111")).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.add(getFeatureFlag("222")).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.listByPage(0, 1, "")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_listAllByPage_with_featureKey_success() {
        featureFlags.add(getFeatureFlag("111")).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.add(getFeatureFlag("222")).block();
        Mono.delay(Duration.ofMillis(200)).block();

        featureFlags.listByPage(0, 2, "11")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    private static FeatureFlag getFeatureFlag(String featureKey) {
        return new FeatureFlag(featureKey, new FeatureFlag.FeatureFlagDescription("feature Name",
                "description",
                DRAFT,
                new FeatureFlag.FeatureConfigTemplate("key", "name", "description", FeatureFlag.DataType.STRING,
                         "defaultValue", null)));
    }
}