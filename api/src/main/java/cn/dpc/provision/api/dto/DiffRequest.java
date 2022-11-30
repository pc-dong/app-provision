package cn.dpc.provision.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Data
@EqualsAndHashCode(callSuper=false)
public class DiffRequest extends RealTimeRequest{
    private String types;
    private String versions;

    public record TypeVersion(String type, String version) {}
    public List<TypeVersion> getTypeVersionList() {
        List<String> categories = Arrays.asList(types.split(",", -1));
        List<String> versions = Arrays.asList(this.versions.split(",", -1));

        List<TypeVersion> result = new ArrayList<>();

        if (categories.contains("")) {
            return result;
        }

        if (categories.size() != versions.size()) {
            return result;
        }

        IntStream.range(0, categories.size())
                .forEach(index -> {
                    result.add(new TypeVersion(categories.get(index), versions.get(index)));
                });
        return result;
    }
}
