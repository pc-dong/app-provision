package cn.dpc.provision.persistence;

import cn.dpc.provision.TestConfiguration;
import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import cn.dpc.provision.domain.exception.StatusNotMatchException;
import cn.dpc.provision.persistence.repository.ConfigurationDB;
import cn.dpc.provision.persistence.repository.ConfigurationDBRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestConfiguration.class)
@ActiveProfiles("test")
class ConfigurationsImplTest {
    @Autowired
    ConfigurationDBRepository repository;

    @Autowired
    CouchbaseTemplate couchbaseTemplate;

    @Autowired
    Configurations configurations;

    @BeforeEach
    public void setUp() {
        repository.deleteAll().then().block(Duration.ofMillis(200));
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll().block(Duration.ofMillis(200));
        Mono.delay(Duration.ofMillis(200)).block();
    }

    @Test
    public void should_add_success() {
        configurations.add(generateConfiguration(null, null))
                .as(StepVerifier::create)
                .expectNextMatches(configuration -> configuration.getId() != null
                        && configuration.getId().getId().length() > 0
                        && configuration.getDescription().getUpdatedAt() != null
                        && configuration.getDescription().getStaticStatus() == StaticStatus.DRAFT)
                .verifyComplete();
    }

    @Test
    public void should_update_throw_error_when_record_not_exist() {
        configurations.update(Configuration.ConfigurationId.of("notexist"), generateConfiguration(null, null))
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_update_success() {
        Configuration configuration = configurations.add(generateConfiguration(null, null))
                .block();

        configurations.update(configuration.getId(), generateConfiguration(null, "key1"))
                .as(StepVerifier::create)
                .expectNextMatches(configuration1 -> configuration1.getDescription().getKey().equals("key1"))
                .verifyComplete();
    }

    @Test
    public void should_getById_throw_error_when_id_not_exist() {
        configurations.getById(Configuration.ConfigurationId.of("notexist"))
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_getById_success() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1"))
                .block();

        configurations.getById(configuration.getId())
                .as(StepVerifier::create)
                .expectNextMatches(configuration1 -> configuration1.getDescription().getKey().equals("key1"))
                .verifyComplete();
    }

    @Test
    public void should_publish_throw_error_when_id_not_exist() {
        configurations.publish(Configuration.ConfigurationId.of("notexist"))
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_publish_throw_error_when_status_is_disabled() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1", StaticStatus.DISABLED))
                .block();

        configurations.publish(configuration.getId())
                .as(StepVerifier::create)
                .expectError(StatusNotMatchException.class)
                .verify();
    }

    @Test
    public void should_publish_throw_error_when_status_is_deleted() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1", StaticStatus.DELETED))
                .block();

        configurations.publish(configuration.getId())
                .as(StepVerifier::create)
                .expectError(StatusNotMatchException.class)
                .verify();
    }

    @Test
    public void should_publish_success() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1"))
                .block();

        configurations.publish(configuration.getId())
                .as(StepVerifier::create)
                .verifyComplete();

        Configuration configuration1 = configurations.getById(configuration.getId()).block();
        assertEquals(configuration1.getDescription().getStaticStatus(), StaticStatus.PUBLISHED);
    }

    @Test
    public void should_disable_throw_error_when_id_not_exist() {
        configurations.disable(Configuration.ConfigurationId.of("notexist"))
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_disable_success() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1"))
                .block();

        configurations.disable(configuration.getId())
                .as(StepVerifier::create)
                .verifyComplete();

        Configuration configuration1 = configurations.getById(configuration.getId()).block();
        assertEquals(configuration1.getDescription().getStaticStatus(), StaticStatus.DISABLED);
    }

    @Test
    public void should_delete_throw_error_when_id_not_exist() {
        configurations.delete(Configuration.ConfigurationId.of("notexist"))
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_delete_success() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1"))
                .block();

        configurations.delete(configuration.getId())
                .as(StepVerifier::create)
                .verifyComplete();

        configurations.getById(configuration.getId())
                .as(StepVerifier::create)
                .expectError(ConfigurationNotFoundException.class)
                .verify();
    }

    @Test
    public void should_find_by_type_success() {
        configurations.add(generateConfiguration(null, "key1", StaticStatus.DRAFT))
                .block();
        configurations.add(generateConfiguration(null, "key1", StaticStatus.PUBLISHED))
                .block();
        configurations.add(generateConfiguration(null, "key1", StaticStatus.DISABLED))
                .block();
        configurations.add(generateConfiguration(null, "key1", StaticStatus.DELETED))
                .block();
        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.findAllByType("type")
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void should_find_available_success() {
        Configuration configuration1 = configurations.add(generateConfiguration(null, "key1", StaticStatus.DRAFT,
                        new TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        configurations.add(generateConfiguration(null, "key1", StaticStatus.DRAFT, new TimeRange(null, LocalDateTime.now().minusHours(1))))
                .block();

        Configuration configuration2 = configurations.add(generateConfiguration(null, "key1", StaticStatus.PUBLISHED,
                        new TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        configurations.add(generateConfiguration(null, "key1", StaticStatus.PUBLISHED,
                        new TimeRange(null, LocalDateTime.now().minusHours(1))))
                .block();

        configurations.add(generateConfiguration(null, "key1", StaticStatus.DISABLED))
                .block();
        configurations.add(generateConfiguration(null, "key1", StaticStatus.DELETED))
                .block();
        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.findAllAvailableByType("type")
                .map(conf -> conf.getId().getId())
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(list ->
                        list.size() == 2
                                && list.contains(configuration1.getId().getId())
                                && list.contains(configuration2.getId().getId()))
                .verifyComplete();
    }

    @Test
    public void should_priority_success() {

        Configuration configuration1 = configurations.add(generateConfiguration(null, "key1", StaticStatus.PUBLISHED,
                        new TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        Configuration configuration2 = configurations.add(generateConfiguration(null, "key1", StaticStatus.PUBLISHED,
                        new TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        Configuration configuration3 = configurations.add(generateConfiguration(null, "key1", StaticStatus.PUBLISHED,
                        new TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        List<Configuration.ConfigurationId> ids = List.of(configuration2.getId(), configuration3.getId(), configuration1.getId());


        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.priority(ids)
                .as(StepVerifier::create)
                .verifyComplete();

        configurations.getById(configuration1.getId())
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getDescription().getPriority() == 2);

        configurations.getById(configuration2.getId())
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getDescription().getPriority() == 0);

        configurations.getById(configuration3.getId())
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getDescription().getPriority() == 1);
    }


    private static Configuration generateConfiguration(String id, String key) {
        return generateConfiguration(id, key, null);
    }

    private static Configuration generateConfiguration(String id, String key, StaticStatus staticStatus) {
        return generateConfiguration(id, key, staticStatus, null);
    }

    private static Configuration generateConfiguration(String id, String key, StaticStatus staticStatus, TimeRange timeRange) {
        Configuration configuration = new Configuration(null == id ? null : Configuration.ConfigurationId.of(id), "type", ConfigurationDescription.builder()
                .createdAt(LocalDateTime.now())
                .data(Map.of("a", "b"))
                .key(key)
                .timeRange(timeRange)
                .staticStatus(staticStatus)
                .build(), new CustomerCriteriaCondition());
        return configuration;
    }
}