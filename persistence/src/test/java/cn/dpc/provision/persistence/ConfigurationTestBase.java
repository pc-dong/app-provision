package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.StaticStatus;
import cn.dpc.provision.domain.TimeRange;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import cn.dpc.provision.persistence.repository.ConfigurationDB;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ConfigurationTestBase {
    protected static Configuration generateConfiguration(String id, String key) {
        return generateConfiguration(id, key, null);
    }

    protected static Configuration generateConfiguration(String id, String key, StaticStatus staticStatus) {
        return generateConfiguration(id, key, staticStatus, null);
    }

    protected static Configuration generateConfiguration(String id, String key, StaticStatus staticStatus, TimeRange timeRange) {
        Configuration configuration = new Configuration(null == id ? null : Configuration.ConfigurationId.of(id), "type", ConfigurationDescription.builder()
                .createdAt(LocalDateTime.now())
                .data(Map.of("a", "b"))
                .key(key)
                .timeRange(timeRange)
                .staticStatus(staticStatus)
                .build(), new CustomerCriteriaCondition());
        return configuration;
    }

    protected static ConfigurationDB generateConfigurationDB(String id, String key, StaticStatus staticStatus, TimeRange timeRange) {
        id = id == null ? UUID.randomUUID().toString() : id;
        return ConfigurationDB.from(generateConfiguration(id, key, staticStatus, timeRange));
    }
}
