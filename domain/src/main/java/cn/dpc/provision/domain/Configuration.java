package cn.dpc.provision.domain;

import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Configuration {
    private ConfigurationId id;

    private String type;

    private ConfigurationDescription description;

    private CustomerCriteriaCondition customerCriteriaCondition;

    @Getter
    @AllArgsConstructor
    public static class ConfigurationId {
        private String id;

        public static ConfigurationId of(String id) {
            return new ConfigurationId(id);
        }
    }
}
