package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ConfigurationTestBase {
    protected static Configuration generateConfiguration(String id, String key,
                                                         HashMap<Object, Object> data, ConfigurationDescription.StaticStatus staticStatus,
                                                         ConfigurationDescription.TimeRange timeRange, CustomerCriteriaCondition condition,
                                                         String type, LocalDateTime updatedAt) {
       return new Configuration(new Configuration.ConfigurationId(id), type, ConfigurationDescription.builder()
                .key(key)
                .staticStatus(staticStatus)
                .timeRange(timeRange)
                .data(data)
                .createdAt(updatedAt)
                .updatedAt(updatedAt)
                .build(), condition);
    }
}
