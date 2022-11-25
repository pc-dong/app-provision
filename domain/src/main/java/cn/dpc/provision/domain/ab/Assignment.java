package cn.dpc.provision.domain.ab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    private String experimentId;
    private String bucketKey;
}
