package cn.dpc.provision.domain.differ;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DifferFactory {
    private final FullDiffer fullDiffer;
    private final IncrementalDiffer incrementalDiffer;


    public ConfigurationDiffer getByType(String type) {
        if(type.startsWith("FULL_")) {
            return fullDiffer;
        }

        if(type.startsWith("INCR_")) {
            return incrementalDiffer;
        }

        return fullDiffer;
    }
}
