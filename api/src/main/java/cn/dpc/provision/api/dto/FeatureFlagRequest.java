package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.FeatureFlag;
import cn.dpc.provision.domain.FeatureFlag.FeatureFlagDescription;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Valid
@Data
public class FeatureFlagRequest {
    @NotBlank
    private String featureKey;

    @NotBlank
    private String name;
    private String description;

    private FeatureFlagDescription.Status status = FeatureFlagDescription.Status.DRAFT;

    @Valid
    @NotNull
    private FeatureFlag.FeatureConfigTemplate template;

    public FeatureFlag toFeatureFlag() {
        return new FeatureFlag(featureKey, new FeatureFlagDescription(name,
                description,
                Optional.ofNullable(status).orElse(FeatureFlagDescription.Status.DRAFT),
                template));
    }
}
