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
                                                DataType dataType,
                                                Object defaultValue,
                                                Status status,
                                                FeatureConfigTemplate template) {
        public enum DataType {
            BOOLEAN, STRING, NUMBER, JSON
        }

        public enum Status {
            DRAFT, PUBLISHED, DISABLED, DELETED
        }
    }

    public record FeatureConfigTemplate(List<Item> items) {
        public enum DataType {
            BOOLEAN, STRING, NUMBER, OBJECT, LIST_STRING, LIST_NUMBER, LIST_OBJECT
        }
        public record Item(String key, String name, String description, DataType dataType, Object defaultValue, List<Item> subItems) {
        }
    }

}
