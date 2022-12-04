package cn.dpc.provision.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiffRSocketRequest {
    private String type;
    private String version;
}
