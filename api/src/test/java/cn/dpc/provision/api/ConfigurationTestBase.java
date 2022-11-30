package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.ConfigurationRequest;
import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.DisplayRule;
import cn.dpc.provision.domain.StaticStatus;
import cn.dpc.provision.domain.TimeRange;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfigurationTestBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    protected ConfigurationRequest generateConfigurationRequest(String type) {
        return generateConfigurationRequest(type, StaticStatus.DRAFT);
    }

    @SneakyThrows
    protected ConfigurationRequest generateConfigurationRequest(String type, StaticStatus status) {
        var request = new ConfigurationRequest();
        request.setType(type);
        request.setKey("TEST_KEY");
        request.setData(objectMapper.readValue("{\"a\": \"b\"}", Object.class));
        request.setTrackingData(objectMapper.readValue("{\"c\": \"d\"}", Object.class));
        request.setTitle("title");
        request.setDescription("description");
        request.setStaticStatus(status);
        request.setDisplayRule(new DisplayRule());
        request.setTimeRange(new TimeRange(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        request.setCustomerCriteriaCondition(new CustomerCriteriaCondition());
        return request;
    }

    @SneakyThrows
    protected Configuration generateConfiguration(String type) {
        return generateConfiguration(type, StaticStatus.DRAFT);
    }

    protected Configuration generateConfiguration(String type, StaticStatus status) {
        Configuration configuration = generateConfigurationRequest(type, status).toConfiguration();
        configuration.setId(new Configuration.ConfigurationId(UUID.randomUUID().toString()));
        return configuration;
    }
}
