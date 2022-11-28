package cn.dpc.provision.api.dto;

import cn.dpc.provision.domain.differ.DifferResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiffResponse {
    private String type;
    private DifferResult differResult;
}
