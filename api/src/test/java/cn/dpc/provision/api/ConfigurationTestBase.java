package cn.dpc.provision.api;

import cn.dpc.provision.api.dto.ConfigurationRequest;
import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfigurationTestBase {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    protected ConfigurationRequest generateConfigurationRequest(String type) {
        return generateConfigurationRequest(type, ConfigurationDescription.StaticStatus.DRAFT);
    }

    @SneakyThrows
    public static ConfigurationRequest generateConfigurationRequest(String type, ConfigurationDescription.StaticStatus status) {
        var request = new ConfigurationRequest();
        request.setType(type);
        request.setKey("TEST_KEY");
        request.setData(objectMapper.readValue("{\"a\": \"b\"}", Object.class));
        request.setTrackingData(objectMapper.readValue("{\"c\": \"d\"}", Object.class));
        request.setTitle("title");
        request.setDescription("description");
        request.setStaticStatus(status);
        request.setDisplayRule(new ConfigurationDescription.DisplayRule());
        request.setTimeRange(new ConfigurationDescription.TimeRange(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));
        request.setCustomerCriteriaCondition(new CustomerCriteriaCondition());
        return request;
    }

    @SneakyThrows
    public static Configuration generateConfiguration(String type) {
        return generateConfiguration(type, ConfigurationDescription.StaticStatus.DRAFT);
    }

    public static Configuration generateConfiguration(String type, ConfigurationDescription.StaticStatus status) {
        Configuration configuration = generateConfigurationRequest(type, status).toConfiguration();
        configuration.setId(new Configuration.ConfigurationId(UUID.randomUUID().toString()));
        return configuration;
    }
}
