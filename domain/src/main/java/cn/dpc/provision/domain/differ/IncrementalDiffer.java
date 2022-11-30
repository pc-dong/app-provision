package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.differ.DataVersion.TimeVersionObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static cn.dpc.provision.domain.DynamicStatus.IN_PROGRESS;
import static cn.dpc.provision.domain.differ.DataVersion.empty;
import static cn.dpc.provision.domain.differ.DifferResult.HAS_NO_UPDATE;
import static cn.dpc.provision.domain.differ.DifferResult.USE_DEFAULT;

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
        if (configurations.isEmpty()) {
            return empty == dataVersion ? HAS_NO_UPDATE : USE_DEFAULT;
        }

        LocalDateTime lastUpdateTime = configurations.stream().map(Configuration::getDescription)
                .map(ConfigurationDescription::getUpdatedAt)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Function.identity()))
                .orElse(null);


        LocalDateTime lastStartTime = configurations.stream().map(Configuration::getDescription)
                .map(ConfigurationDescription::getTimeRange)
                .filter(Objects::nonNull)
                .map(TimeRange::getStartDate)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Function.identity()))
                .orElse(null);

        DataVersion resultVersion = new DataVersion(TimeVersionObject.create(lastUpdateTime, lastStartTime).toString(), loginStatus);
        if (resultVersion.equals(dataVersion)) {
            return HAS_NO_UPDATE;
        }

        TimeVersionObject timeVersionObject = dataVersion.getLoginStatus() != loginStatus
                ? TimeVersionObject.builder().build()
                : TimeVersionObject.from(dataVersion.getVersion());
        List<DifferResult.DifferContent> contents = DifferResult.DifferContent.from(configurations, configuration -> configuration.getDescription().getStatus() == IN_PROGRESS
                && timeVersionObject.hasUpdated(configuration.getDescription().getUpdatedAt(),
                Optional.ofNullable(configuration.getDescription().getTimeRange()).map(TimeRange::getStartDate).orElse(null)));

        return new DifferResult(true, false, contents, resultVersion.toString());
    }
}
