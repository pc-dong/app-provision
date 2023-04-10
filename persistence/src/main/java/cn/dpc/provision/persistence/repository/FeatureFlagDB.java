package cn.dpc.provision.persistence.repository;

import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import static cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription.Status.*;

@Document
@Data
public class FeatureFlagDB {
    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;

    @Field
    @QueryIndexed
    private String featureKey;

    @Field
    private FeatureFlagDescription description;

    public static FeatureFlagDB from(FeatureFlag featureFlag) {
        FeatureFlagDB db = new FeatureFlagDB();
        BeanUtils.copyProperties(featureFlag, db);
        db.setId(featureFlag.getId().getId());
        return db;
    }

    public FeatureFlag to() {
        return new FeatureFlag(featureKey, description);
    }

    public FeatureFlagDB update(FeatureFlagDescription description) {
        this.description = description;
        return this;
    }

    public FeatureFlagDB publish() {
        return updateStatus(PUBLISHED);
    }

    public FeatureFlagDB disable() {
        return updateStatus(DISABLED);
    }

    public FeatureFlagDB delete() {
        return updateStatus(DELETED);
    }

    private FeatureFlagDB updateStatus(FeatureFlagDescription.Status status) {
        this.setDescription(new FeatureFlagDescription(
                this.getDescription().name(),
                this.getDescription().description(),
                this.getDescription().dataType(),
                this.getDescription().defaultValue(),
                status,
                this.getDescription().template()));
        return this;
    }
}
