package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Valid
@Data
public class FeatureFlagRequest {
    @NotBlank
    private String featureKey;

    @NotNull
    @Valid
    private FeatureFlagDescription description;

    public FeatureFlag toFeatureFlag() {
        return new FeatureFlag(featureKey, description);
    }
}
