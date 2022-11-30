package cn.dpc.provision.domain.differ;

import cn.dpc.provision.domain.*;
import cn.dpc.provision.domain.differ.DataVersion.LoginStatus;
import cn.dpc.provision.domain.differ.DifferResult.DifferContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static cn.dpc.provision.domain.DynamicStatus.IN_PROGRESS;
import static cn.dpc.provision.domain.differ.DataVersion.empty;
import static cn.dpc.provision.domain.differ.DifferResult.HAS_NO_UPDATE;
import static cn.dpc.provision.domain.differ.DifferResult.USE_DEFAULT;

@RequiredArgsConstructor
@Component
public class FullDiffer implements ConfigurationDiffer {
    private final CustomerConfigurations customerConfigurations;

    @Override
    public Mono<DifferResult> diff(String type, String version, Customer customer) {
        DataVersion dataVersion = DataVersion.parseDataVersion(version);
        return customerConfigurations.flux(type, customer)
                .filter(configuration -> configuration.getDescription().getStatus() == IN_PROGRESS)
                .collectList()
                .map(configurations -> this.toDifferResult(dataVersion,
                        LoginStatus.getByCustomerId(customer.getCustomerId()), configurations));
    }

    private DifferResult toDifferResult(DataVersion dataVersion, LoginStatus loginStatus, List<Configuration> configurations) {
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

        DataVersion resultVersion = new DataVersion(DataVersion.TimeVersionObject.create(lastUpdateTime, lastStartTime).toString(), loginStatus);
        if (resultVersion.equals(dataVersion)) {
            return HAS_NO_UPDATE;
        }

        List<DifferContent> contents = DifferContent.from(configurations);

        return new DifferResult(true, false, contents, resultVersion.toString());
    }
}
