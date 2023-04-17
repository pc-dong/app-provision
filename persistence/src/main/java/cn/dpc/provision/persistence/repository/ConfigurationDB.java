package cn.dpc.provision.persistence.repository;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Document
@Data
@CompositeQueryIndex(fields = {"_class", "type"})
public class ConfigurationDB {

    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;

    @Field
    @QueryIndexed
    private String type;

    @Field
    private ConfigurationDescriptionDB description;

    private CustomerCriteriaCondition customerCriteriaCondition;

    public static ConfigurationDB from(Configuration configuration) {
        ConfigurationDB db = new ConfigurationDB();
        BeanUtils.copyProperties(configuration, db);
        db.setId(configuration.getId().getId());
        db.setDescription(ConfigurationDescriptionDB.from(configuration.getDescription()));
        return db;
    }

    public Configuration to() {
        return new Configuration(Configuration.ConfigurationId.of(this.getId()), this.getType(), this.getDescription().to(), this.getCustomerCriteriaCondition());
    }

    public ConfigurationDB updateFrom(Configuration configuration) {
        this.getDescription().setUpdatedAt(LocalDateTime.now());
        this.getDescription().setStaticStatus(configuration.getDescription().getStaticStatus());
        this.getDescription().setPublicData(configuration.getDescription().getData());
        this.getDescription().setKey(configuration.getDescription().getKey());
        this.getDescription().setTrackingData(configuration.getDescription().getTrackingData());
        this.getDescription().setPriority(configuration.getDescription().getPriority());
        this.getDescription().setDisplayRule(configuration.getDescription().getDisplayRule());
        this.getDescription().setTimeRange(configuration.getDescription().getTimeRange());
        this.getDescription().setDescription(configuration.getDescription().getDescription());
        this.getDescription().setTitle(configuration.getDescription().getTitle());
        this.setCustomerCriteriaCondition(configuration.getCustomerCriteriaCondition());
        return this;
    }

    public ConfigurationDB updateStatus(ConfigurationDescription.StaticStatus staticStatus) {
        this.getDescription().setStaticStatus(staticStatus);
        this.getDescription().setUpdatedAt(LocalDateTime.now());
        return this;
    }

    public ConfigurationDB updatePriority(Long value) {
        this.getDescription().setPriority(value);
        this.getDescription().setUpdatedAt(LocalDateTime.now());
        return this;
    }

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class ConfigurationDescriptionDB {
        private String title;
        private String description;
        private String key;
        private Object data;
        private ArrayList arrayData;
        private Object trackingData;
        private cn.dpc.provision.domain.ConfigurationDescription.StaticStatus staticStatus;
        private cn.dpc.provision.domain.ConfigurationDescription.TimeRange timeRange;
        private cn.dpc.provision.domain.ConfigurationDescription.DisplayRule displayRule;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private long priority;

        @Transient
        public void setPublicData(Object data) {
            if (null == data) {
                this.arrayData = null;
                this.data = null;
                return;
            }

            if (data instanceof ArrayList) {
                this.data = null;
                this.arrayData = (ArrayList) data;
                return;
            }

            this.data = data;
            this.arrayData = null;
        }

        @Transient
        public Object getPublicData() {
            if (null != this.arrayData) {
                return arrayData;
            }

            return this.data;
        }

        public ConfigurationDescription to() {
            ConfigurationDescription configurationDescription = new ConfigurationDescription();
            BeanUtils.copyProperties(this, configurationDescription);
            configurationDescription.setData(this.getPublicData());
            return configurationDescription;
        }

        public static ConfigurationDescriptionDB from(ConfigurationDescription description) {
            ConfigurationDescriptionDB configurationDescriptionDB = new ConfigurationDescriptionDB();
            BeanUtils.copyProperties(description, configurationDescriptionDB);
            configurationDescriptionDB.setPublicData(description.getData());
            return configurationDescriptionDB;
        }
    }
}
