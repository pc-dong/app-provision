package cn.dpc.provision.persistence;

import cn.dpc.provision.TestConfiguration;
import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.Configurations;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import cn.dpc.provision.domain.exception.StatusNotMatchException;
import cn.dpc.provision.persistence.repository.ConfigurationDBRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestConfiguration.class)
@ActiveProfiles("test")
class ConfigurationsImplTest extends ConfigurationTestBase{
    @Autowired
    ConfigurationDBRepository repository;

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
                        && configuration.getDescription().getStaticStatus() == ConfigurationDescription.StaticStatus.DRAFT)
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
        Configuration configuration = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DISABLED))
                .block();

        configurations.publish(configuration.getId())
                .as(StepVerifier::create)
                .expectError(StatusNotMatchException.class)
                .verify();
    }

    @Test
    public void should_publish_throw_error_when_status_is_deleted() {
        Configuration configuration = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DELETED))
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
        assertEquals(configuration1.getDescription().getStaticStatus(), ConfigurationDescription.StaticStatus.PUBLISHED);
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
        assertEquals(configuration1.getDescription().getStaticStatus(), ConfigurationDescription.StaticStatus.DISABLED);
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
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DRAFT))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DISABLED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DELETED))
                .block();
        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.findAllByType("type")
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void should_find_available_success() {
        Configuration configuration1 = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DRAFT,
                        new ConfigurationDescription.TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DRAFT, new ConfigurationDescription.TimeRange(null, LocalDateTime.now().minusHours(1))))
                .block();

        Configuration configuration2 = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED,
                        new ConfigurationDescription.TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED,
                        new ConfigurationDescription.TimeRange(null, LocalDateTime.now().minusHours(1))))
                .block();

        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DISABLED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DELETED))
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

        Configuration configuration1 = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED,
                        new ConfigurationDescription.TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        Configuration configuration2 = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED,
                        new ConfigurationDescription.TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        Configuration configuration3 = configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED,
                        new ConfigurationDescription.TimeRange(null, LocalDateTime.now().plusHours(1))))
                .block();

        List<Configuration.ConfigurationId> ids = List.of(configuration2.getId(), configuration3.getId(), configuration1.getId());


        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.priority(ids)
                .as(StepVerifier::create)
                .verifyComplete();

        configurations.getById(configuration1.getId())
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getDescription().getPriority() == 3)
                .verifyComplete();

        configurations.getById(configuration2.getId())
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getDescription().getPriority() == 1)
                .verifyComplete();

        configurations.getById(configuration3.getId())
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getDescription().getPriority() == 2)
                .verifyComplete();
    }

    @Test
    public void should_findAllByPage_return_empty_when_no_records() {
        configurations.findAllByPage("type", 0, 10, null)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void should_findAllByPage_return_by_page() {
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DRAFT))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DISABLED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DELETED))
                .block();
        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.findAllByPage("type", 0, 2, null)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        configurations.findAllByPage("type", 1, 2, null)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    public void should_countAll_return_0_when_no_records() {
        configurations.countAll("type", null)
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    public void should_countAll_return_records_number() {
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DRAFT))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.PUBLISHED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DISABLED))
                .block();
        configurations.add(generateConfiguration(null, "key1", ConfigurationDescription.StaticStatus.DELETED))
                .block();
        Mono.delay(Duration.ofMillis(1000)).block();

        configurations.countAll("type", null)
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }
}