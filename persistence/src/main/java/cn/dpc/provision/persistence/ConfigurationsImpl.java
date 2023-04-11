package cn.dpc.provision.persistence;

import cn.dpc.provision.domain.Configuration;
import cn.dpc.provision.domain.Configuration.ConfigurationId;
import cn.dpc.provision.domain.ConfigurationDescription;
import cn.dpc.provision.domain.Configurations;
import cn.dpc.provision.domain.exception.ConfigurationNotFoundException;
import cn.dpc.provision.domain.exception.StatusNotMatchException;
import cn.dpc.provision.persistence.repository.ConfigurationDB;
import cn.dpc.provision.persistence.repository.ConfigurationDBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static cn.dpc.provision.domain.ConfigurationDescription.StaticStatus.*;
import static java.util.stream.Collectors.toMap;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ConfigurationsImpl implements Configurations {
    private final ConfigurationDBRepository repository;
    @Override
    public Flux<Configuration> findAllByType(String type) {
        return repository.findByType(type, Sort.by(new Order(DESC, "description.updatedAt")))
                .filter(configurationDB -> configurationDB.getDescription().getStaticStatus() != ConfigurationDescription.StaticStatus.DELETED)
                .map(ConfigurationDB::to);
    }

    @Override
    public Mono<Configuration> getById(ConfigurationId id) {
        return repository.findById(id.getId())
                .flatMap(db -> db.getDescription().getStaticStatus() == DELETED ? Mono.empty() : Mono.just(db.to()))
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(id.getId())));
    }

    @Override
    public Mono<Configuration> add(Configuration configuration) {
        configuration.setId(ConfigurationId.of(UUID.randomUUID().toString()));
        configuration.getDescription().setUpdatedAt(LocalDateTime.now());
        configuration.getDescription().setCreatedAt(LocalDateTime.now());
        if (configuration.getDescription().getStaticStatus() == null) {
            configuration.getDescription().setStaticStatus(DRAFT);
        }

        return repository.save(ConfigurationDB.from(configuration))
                .map(ConfigurationDB::to);
    }

    @Override
    public Mono<Configuration> update(ConfigurationId id, Configuration configuration) {
        return repository.findById(id.getId())
                .map(db -> db.updateFrom(configuration))
                .flatMap(repository::save)
                .map(ConfigurationDB::to)
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(id.getId())));
    }

    @Override
    public Mono<Void> publish(ConfigurationId id) {
        return updateStatus(id, PUBLISHED, List.of(DISABLED, DELETED));
    }

    @Override
    public Mono<Void> disable(ConfigurationId id) {
        return updateStatus(id, DISABLED);
    }

    @Override
    public Mono<Void> delete(ConfigurationId id) {
        return updateStatus(id, DELETED);
    }

    @Override
    public Flux<Configuration> findAllAvailableByType(String type) {
        return repository.findByType(type, Sort.by(new Order(ASC, "description.priority"),
                        new Order(DESC, "description.updatedAt")))
                .filter(db -> db.getDescription().isAvailable())
                .map(ConfigurationDB::to);
    }

    @Override
    @Transactional
    public Mono<Void> priority(List<ConfigurationId> ids) {
        return Flux.fromIterable(IntStream.range(0, ids.size()).boxed()
                        .collect(toMap(index -> ids.get(index).getId(), i -> i + 1)).entrySet())
                .flatMap(idAndPriority ->
                        repository.findById(idAndPriority.getKey())
                                .map(db -> db.updatePriority(Long.valueOf(idAndPriority.getValue())))
                )
                .transformDeferred(repository::saveAll).then();
    }

    @Override
    public Flux<Configuration> findAllByPage(String type, int page, int pageSize, String key) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page);
        return repository.findAllByPage(type, key, pageable.getOffset(), pageable.getPageSize())
                .map(ConfigurationDB::to);
    }

    @Override
    public Mono<Long> countAll(String type, String key) {
        return repository.countAll(type, key);
    }

    private Mono<Void> updateStatus(ConfigurationId id, ConfigurationDescription.StaticStatus staticStatus) {
        return this.updateStatus(id, staticStatus, List.of());
    }

    private Mono<Void> updateStatus(ConfigurationId id, ConfigurationDescription.StaticStatus staticStatus, List<ConfigurationDescription.StaticStatus> deniedStatus) {
        return repository.findById(id.getId())
                .switchIfEmpty(Mono.error(new ConfigurationNotFoundException(id.getId())))
                .map(db -> {
                    if (deniedStatus.contains(db.getDescription().getStaticStatus())) {
                        throw new StatusNotMatchException(id.getId());
                    }
                    return db.updateStatus(staticStatus);
                })
                .flatMap(repository::save)
                .then();
    }
}
