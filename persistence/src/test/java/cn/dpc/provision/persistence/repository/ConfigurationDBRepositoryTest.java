package cn.dpc.provision.persistence.repository;

import cn.dpc.provision.TestConfiguration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestConfiguration.class)
@ActiveProfiles("test")
class ConfigurationDBRepositoryTest {
    @Autowired
    private ConfigurationDBRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll().block();
        Mono.delay(Duration.ofMillis(200)).block();
        List<ConfigurationDB> dbs = List.of(generateConfiguration("1", LocalDateTime.now()),
                generateConfiguration("2", LocalDateTime.now().plusHours(1)),
                generateConfiguration("3", LocalDateTime.now().plusHours(2)));
        repository.saveAll(dbs).blockLast();
        Mono.delay(Duration.ofMillis(200)).block();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll().block();
        Mono.delay(Duration.ofMillis(200)).block();
    }

    @Test
    public void should_findByType_success() {
        repository.findByType("type", Sort.by(Sort.Order.desc("description.updatedAt")))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getId().equals("3"))
                .expectNextMatches(item -> item.getId().equals("2"))
                .expectNextMatches(item -> item.getId().equals("1"))
                .verifyComplete();
    }

    private static ConfigurationDB generateConfiguration(String id, LocalDateTime updatedAt) {
        ConfigurationDB configurationDB = new ConfigurationDB();
        configurationDB.setId(id);
        configurationDB.setType("type");
        configurationDB.setDescription(ConfigurationDescription.builder()
                        .updatedAt(updatedAt)
                        .createdAt(LocalDateTime.now())
                        .data(Map.of("a", "b"))
                        .key("key")
                .build());
        configurationDB.setCustomerCriteriaCondition(new CustomerCriteriaCondition());
        return configurationDB;
    }

}