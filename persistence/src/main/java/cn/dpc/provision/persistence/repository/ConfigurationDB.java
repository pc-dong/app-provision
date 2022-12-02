package cn.dpc.provision.persistence.repository;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.StaticStatus;
import cn.dpc.provision.domain.condition.CustomerCriteriaCondition;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;

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
    private ConfigurationDescription description;

    private CustomerCriteriaCondition customerCriteriaCondition;

    public static ConfigurationDB from(Configuration configuration) {
        ConfigurationDB db = new ConfigurationDB();
        BeanUtils.copyProperties(configuration, db);
        db.setId(configuration.getId().getId());
        return db;
    }

    public Configuration to() {
        return new Configuration(Configuration.ConfigurationId.of(this.getId()), this.getType(), this.getDescription(), this.getCustomerCriteriaCondition());
    }

    public ConfigurationDB updateFrom(Configuration configuration) {
        this.getDescription().setUpdatedAt(LocalDateTime.now());
        this.getDescription().setStaticStatus(configuration.getDescription().getStaticStatus());
        this.getDescription().setData(configuration.getDescription().getData());
        this.getDescription().setKey(configuration.getDescription().getKey());
        this.getDescription().setTrackingData(configuration.getDescription().getTrackingData());
        this.getDescription().setPriority(configuration.getDescription().getPriority());
        this.getDescription().setDisplayRule(configuration.getDescription().getDisplayRule());
        this.getDescription().setTimeRange(configuration.getDescription().getTimeRange());
        this.getDescription().setDescription(configuration.getDescription().getDescription());
        this.getDescription().setTitle(configuration.getDescription().getTitle());
        return this;
    }

    public ConfigurationDB updateStatus(StaticStatus staticStatus) {
        this.getDescription().setStaticStatus(staticStatus);
        this.getDescription().setUpdatedAt(LocalDateTime.now());
        return this;
    }

    public ConfigurationDB updatePriority(Long value) {
        this.getDescription().setPriority(value);
        this.getDescription().setUpdatedAt(LocalDateTime.now());
        return this;
    }
}
