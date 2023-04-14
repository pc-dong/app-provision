package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import lombok.Data;


@Data
public class FeatureFlagResponse {

    private String id;

    private String featureKey;

    private String name;
    private String description;
    private FeatureFlagDescription.Status status = FeatureFlagDescription.Status.DRAFT;
    private FeatureFlag.FeatureConfigTemplate template;

    public static FeatureFlagResponse fromFeatureFlag(FeatureFlag featureFlag) {
        FeatureFlagResponse response = new FeatureFlagResponse();
        response.id = featureFlag.getId().getId();
        response.featureKey = featureFlag.getFeatureKey();
        response.name = featureFlag.getDescription().name();
        response.description = featureFlag.getDescription().description();
        response.status = featureFlag.getDescription().status();
        response.template = featureFlag.getDescription().template();
        return response;
    }
}
