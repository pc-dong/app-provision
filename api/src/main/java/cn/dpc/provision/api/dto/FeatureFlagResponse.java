package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class FeatureFlagResponse {

    @NotBlank
    private String id;

    @NotBlank
    private String featureKey;

    @Valid
    private FeatureFlagDescription description;

    public static FeatureFlagResponse fromFeatureFlag(FeatureFlag featureFlag) {
        FeatureFlagResponse response = new FeatureFlagResponse();
        response.id = featureFlag.getId().getId();
        response.featureKey = featureFlag.getFeatureKey();
        response.description = featureFlag.getDescription();
        return response;
    }
}
