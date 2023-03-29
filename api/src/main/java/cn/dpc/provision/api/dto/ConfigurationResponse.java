package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationResponse {
    private String id;
    private String type;
    private String title;
    private String description;
    private String key;
    private Object data;
    private Object trackingData;
    private ConfigurationDescription.StaticStatus staticStatus;
    private ConfigurationDescription.DynamicStatus status;
    private ConfigurationDescription.TimeRange timeRange;
    private ConfigurationDescription.DisplayRule displayRule;
    private long priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CustomerCriteriaCondition customerCriteriaCondition;
    public static ConfigurationResponse from(Configuration configuration) {
        return ConfigurationResponse.builder()
                .id(configuration.getId().getId())
                .type(configuration.getType())
                .title(configuration.getDescription().getTitle())
                .description(configuration.getDescription().getDescription())
                .key(configuration.getDescription().getKey())
                .data(configuration.getDescription().getData())
                .trackingData(configuration.getDescription().getTrackingData())
                .staticStatus(configuration.getDescription().getStaticStatus())
                .status(configuration.getDescription().getStatus())
                .timeRange(configuration.getDescription().getTimeRange())
                .displayRule(configuration.getDescription().getDisplayRule())
                .priority(configuration.getDescription().getPriority())
                .createdAt(configuration.getDescription().getCreatedAt())
                .updatedAt(configuration.getDescription().getUpdatedAt())
                .customerCriteriaCondition(configuration.getCustomerCriteriaCondition())
                .build();
    }
}
