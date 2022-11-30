package cn.dpc.provision.domain;

import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
public class Configuration {
    @Setter
    private ConfigurationId id;

    private String type;

    private ConfigurationDescription description;

    private CustomerCriteriaCondition customerCriteriaCondition;

    public Configuration(String type, ConfigurationDescription description, CustomerCriteriaCondition customerCriteriaCondition) {
        this(null, type, description, customerCriteriaCondition);
    }

    public Configuration(ConfigurationId id, String type, ConfigurationDescription description, CustomerCriteriaCondition customerCriteriaCondition) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.customerCriteriaCondition = customerCriteriaCondition;
        if(StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("type can not be empty");
        }
    }



    @Getter
    @AllArgsConstructor
    public static class ConfigurationId {
        private String id;

        public static ConfigurationId of(String id) {
            return new ConfigurationId(id);
        }
    }
}
