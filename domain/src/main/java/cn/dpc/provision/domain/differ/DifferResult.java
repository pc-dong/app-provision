package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.ConfigurationDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifferResult {
    public static final DifferResult USE_DEFAULT = new DifferResult(true, true);
    public static final DifferResult HAS_NO_UPDATE = new DifferResult(false, false);


    public DifferResult(boolean hasUpdate, boolean useDefault) {
        this.hasUpdate = hasUpdate;
        this.useDefault = useDefault;
    }

    private boolean hasUpdate;

    private boolean useDefault;

    private List<DifferContent> content;

    private String version;

    public record DifferItem(String id,
                             String key,
                             Object data,
                             Object trackingData,
                             ConfigurationDescription.DisplayRule displayRule,
                             LocalDateTime updatedAt,
                             LocalDateTime expiredAt,
                             long priority) {
        public static DifferResult.DifferItem from(Configuration configuration) {
            return new DifferResult.DifferItem(configuration.getId().getId(),
                    configuration.getDescription().getKey(),
                    configuration.getDescription().getData(),
                    configuration.getDescription().getTrackingData(),
                    configuration.getDescription().getDisplayRule(),
                    configuration.getDescription().getUpdatedAt(),
                    Optional.ofNullable(configuration.getDescription().getTimeRange()).map(ConfigurationDescription.TimeRange::getEndDate)
                            .orElse(null),
                    configuration.getDescription().getPriority());
        }
    }

    ;

    public record DifferContent(String key,
                                long priority,
                                LocalDateTime updatedAt,
                                List<DifferItem> items) {

        public static List<DifferResult.DifferContent> from(List<Configuration> configurations, Predicate<Configuration> predicate) {
            return configurations.stream().collect(Collectors.groupingBy((configuration -> configuration.getDescription().getKey())))
                    .entrySet().stream().map(entry -> {
                        String key = entry.getKey();
                        List<DifferResult.DifferItem> items = entry.getValue().stream()
                                .filter(predicate)
                                .map(DifferItem::from)
                                .sorted(Comparator.comparing(DifferResult.DifferItem::priority)
                                        .thenComparing(DifferResult.DifferItem::updatedAt, reverseOrder()))
                                .collect(Collectors.toList());
                        return new DifferResult.DifferContent(key, items.isEmpty() ? 0 : items.get(0).priority(),
                                items.isEmpty() ? LocalDateTime.MIN : items.get(0).updatedAt(), items);
                    }).sorted(Comparator.comparing(DifferResult.DifferContent::priority).thenComparing(DifferResult.DifferContent::updatedAt, reverseOrder()))
                    .collect(Collectors.toList());
        }

        public static List<DifferResult.DifferContent> from(List<Configuration> configurations) {
            return from(configurations, configuration -> true);
        }
    }
}
