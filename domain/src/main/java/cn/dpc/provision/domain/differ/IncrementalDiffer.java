package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.differ.DataVersion.TimeVersionObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.dpc.provision.domain.DynamicStatus.IN_PROGRESS;
import static cn.dpc.provision.domain.differ.DataVersion.empty;
import static cn.dpc.provision.domain.differ.DifferResult.HAS_NO_UPDATE;
import static cn.dpc.provision.domain.differ.DifferResult.USE_DEFAULT;
import static java.util.Comparator.reverseOrder;

@RequiredArgsConstructor
@Component
public class IncrementalDiffer implements ConfigurationDiffer {
    private final CustomerConfigurations customerConfigurations;

    @Override
    public Mono<DifferResult> diff(String type, String version, Customer customer) {
        DataVersion dataVersion = DataVersion.parseDataVersion(version);
        return customerConfigurations.flux(type, customer)
                .collectList()
                .map(configurations -> this.toDifferResult(dataVersion,
                        DataVersion.LoginStatus.getByCustomerId(customer.getCustomerId()), configurations));
    }

    private DifferResult toDifferResult(DataVersion dataVersion, DataVersion.LoginStatus loginStatus, List<Configuration> configurations) {
        if(configurations.isEmpty()) {
            return empty == dataVersion ? HAS_NO_UPDATE : USE_DEFAULT;
        }

        LocalDateTime lastUpdateTime = configurations.stream().sorted((Configuration o1, Configuration o2) ->
                o2.getDescription().getUpdatedAt().compareTo(o2.getDescription().getUpdatedAt())
        ).findFirst().map(configuration -> configuration.getDescription().getUpdatedAt()).orElse(null);


        LocalDateTime lastStartTime = configurations.stream().sorted((Configuration o1, Configuration o2) ->
                Optional.ofNullable(o2.getDescription()).map(ConfigurationDescription::getTimeRange)
                        .map(TimeRange::getStartDate).orElse(LocalDateTime.MIN)
                        .compareTo(Optional.ofNullable(o1.getDescription()).map(ConfigurationDescription::getTimeRange)
                                .map(TimeRange::getStartDate).orElse(LocalDateTime.MIN))
        ).findFirst().map(configuration -> configuration.getDescription().getUpdatedAt()).orElse(null);

        DataVersion resultVersion =  new DataVersion(TimeVersionObject.create(lastUpdateTime, lastStartTime).toVersion(), loginStatus);
        if(resultVersion.equals(dataVersion)) {
            return HAS_NO_UPDATE;
        }

        TimeVersionObject timeVersionObject = TimeVersionObject.from(dataVersion.getVersion());
        List<DifferResult.DifferContent> contents = DifferResult.DifferContent.from(configurations, configuration -> configuration.getDescription().getStatus() == IN_PROGRESS
                && timeVersionObject.hasUpdated(configuration.getDescription().getUpdatedAt(),
                Optional.ofNullable(configuration.getDescription().getTimeRange()).map(TimeRange::getStartDate).orElse(null)));

        return new DifferResult(true, false, contents, resultVersion.toString());
    }
}
