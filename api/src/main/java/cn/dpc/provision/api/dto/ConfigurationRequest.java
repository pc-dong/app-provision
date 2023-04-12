package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfigurationRequest {

    @NotBlank(message = "type can not be blank")
    private String type;
    private String title;
    private String description;
    private String key;
    private Object data;
    private Object trackingData;
    private ConfigurationDescription.StaticStatus staticStatus;
    private ConfigurationDescription.TimeRange timeRange;
    private ConfigurationDescription.DisplayRule displayRule;
    private long priority;
    private CustomerCriteriaCondition customerCriteriaCondition;

    public Configuration toConfiguration() {
        var description = ConfigurationDescription.builder()
                .key(key)
                .data(data)
                .title(title)
                .description(this.description)
                .trackingData(trackingData)
                .staticStatus(staticStatus)
                .timeRange(timeRange)
                .displayRule(displayRule)
                .priority(priority)
                .build();
        return new Configuration( type, description, customerCriteriaCondition);
    }
}
