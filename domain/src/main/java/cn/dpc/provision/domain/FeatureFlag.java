package cn.dpc.provision.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class FeatureFlag {
    @Setter
    private FeatureFlagId id;

    private final String featureKey;

    private final FeatureFlagDescription description;

    public FeatureFlag(String featureKey, FeatureFlagDescription description) {
       this.featureKey = featureKey;
       this.description = description;
       this.id = FeatureFlagId.fromFeatureKey(featureKey);
    }

    public static class FeatureFlagId {
        private static final String PREFIX = "feature-flag-";
        @Getter
        private final String id;

        private FeatureFlagId(String id) {
            this.id = id;
        }

        public static FeatureFlagId fromFeatureKey(String featureKey) {
            return new FeatureFlagId(PREFIX + featureKey);
        }
    }

    public record FeatureFlagDescription(String name,
                                                String description,
                                                Status status,
                                                FeatureConfigTemplate template) {

        public enum Status {
            DRAFT, PUBLISHED, DISABLED, DELETED
        }

    }

    public enum DataType {
        BOOLEAN, STRING, NUMBER, OBJECT, ARRAY
    }

    public record FeatureConfigTemplate(String key, String name, String description, DataType dataType, Object defaultValue, List<FeatureConfigTemplate> items) {
    }

}
