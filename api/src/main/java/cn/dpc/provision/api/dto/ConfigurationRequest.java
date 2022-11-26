package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ConfigurationRequest {

    @NotBlank(message = "type can not be blank")
    private String type;
    private String title;
    private String description;
    private String key;
    private Object data;
    private Object trackingData;
    private StaticStatus staticStatus;
    private TimeRange timeRange;
    private DisplayRule displayRule;
    private long priority;
    private CustomerCriteriaCondition customerCriteriaCondition;

    public Configuration toConfiguration() {
        var description = ConfigurationDescription.builder()
                .key(key)
                .data(data)
                .trackingData(trackingData)
                .staticStatus(staticStatus)
                .timeRange(timeRange)
                .displayRule(displayRule)
                .priority(priority)
                .build();
        return new Configuration( type, description, customerCriteriaCondition);
    }
}
