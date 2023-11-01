package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.TypedValue;
import tao.dong.dataconjurer.common.support.CircularDependencyChecker;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataGenerateException;
import tao.dong.dataconjurer.common.support.DataGenerateTask;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.DEPENDENCE;

@Slf4j
public class DataGenerateService {

    private final DataGenerateConfig config;
    private final CircularDependencyChecker circularDependencyChecker;

    public DataGenerateService(DataGenerateConfig config, CircularDependencyChecker circularDependencyChecker) {
        this.config = config;
        this.circularDependencyChecker = circularDependencyChecker;
    }

    public void generateData(@NotEmpty Set<EntityWrapper> entities) {

        validate(entities);
        generateEntityData(entities);
    }

    private void generateEntityData(Set<EntityWrapper> entities) {
        var pool = Executors.newFixedThreadPool(config.getHandlerCount());
        var entityMap = entities.stream().collect(Collectors.toMap(EntityWrapper::getEntityName, Function.identity()));
        try {
            var processed = 0;
            while (processed <= entities.size() - 1) {
                var candidates = findReadyEntities(entities, entityMap);
                var runners = removeDropouts(candidates);
                processed += (candidates.size() - runners.size());
                if (!runners.isEmpty()) {
                    var cleanup = false;
                    try {
                        final var latch = new CountDownLatch(runners.size());
                        var futures = runners.stream()
                                        .map(target -> createDataGenerateTask(target, entityMap, latch))
                                        .map(pool::submit)
                                        .toList();

                        var lrs = latch.await(config.getTimeOut().getSeconds(), TimeUnit.SECONDS);
                        if (lrs) {
                            for (var future : futures) {
                                try {
                                    var rs = future.get();
                                    LOG.info("Data generation completed for {}, status {}", rs.id(), rs.status());
                                } catch (InterruptedException | ExecutionException e) {
                                    LOG.error("Data generation failed", e);
                                    cleanup = true;
                                }
                            }
                        } else {
                            failDataGeneration(runners, "Data generation timeout");
                        }
                    } catch (InterruptedException e) {
                        LOG.error("Data generation is interrupted", e);
                        Thread.currentThread().interrupt();
                        failDataGeneration(runners, "Data generation is interrupted");
                    }
                    if (cleanup) {
                        failDataGeneration(runners, "Data generation failed");
                    }
                    processed += runners.size();
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    private void failDataGeneration(Collection<EntityWrapper> wrappers, String message) {
        for (var wrapper : wrappers) {
            failEntityGeneration(wrapper, message);
        }
    }

    private void failEntityGeneration(EntityWrapper wrapper, String message) {
        if (wrapper.getStatus() != 2) {
            wrapper.failProcess(message);
        }
    }

    private DataGenerateTask createDataGenerateTask(EntityWrapper target, Map<String, EntityWrapper> data, CountDownLatch latch) {
        var referenceValues = getReferencedValues(target, data);
        return DataGenerateTask.builder()
                .config(config)
                .countDownLatch(latch)
                .entityWrapper(target)
                .referenced(referenceValues)
                .build();
    }

    private Map<Reference, TypedValue> getReferencedValues(EntityWrapper target, Map<String, EntityWrapper> data) {
        Map<Reference, TypedValue> referencedValues = new HashMap<>();
        Map<String, Set<String>> references = new HashMap<>();
        for (var ref : target.getReferences().values()) {
            references.compute(ref.entity(), (k, v) -> {
                if (v == null) {
                    var nv = new HashSet<String>();
                    nv.add(ref.property());
                    return nv;
                } else {
                    v.add(ref.property());
                    return v;
                }
            });
        }
        if (!references.isEmpty()) {
            for (var entry : references.entrySet()) {
                referencedValues.putAll(data.get(entry.getKey()).getReferencedByProperties(entry.getValue().toArray(String[]::new)));
            }
        }
        return referencedValues;
    }

    private Set<EntityWrapper> findReadyEntities(Set<EntityWrapper> entityWrappers, Map<String, EntityWrapper> entityMap) {
        Set<EntityWrapper> candidates = new HashSet<>();
        for (var wrapper : entityWrappers) {
            if (wrapper.getStatus() == 0 && wrapper.getDependencies().stream().map(entityMap::get).noneMatch(en -> en.getStatus() != -1 || en.getStatus() != 2)) {
                candidates.add(wrapper);
            }
        }
        return candidates;
    }

    private Set<EntityWrapper> removeDropouts(Set<EntityWrapper> entityWrappers) {
        return entityWrappers.stream()
                .filter(wrapper -> wrapper.getStatus() != -1)
                .collect(Collectors.toSet());
    }


    private void validate(Set<EntityWrapper> wrappers) {
        if (hasCircularDependencies(wrappers)) {
            throw new DataGenerateException(DEPENDENCE, "Circular dependencies found among entities");
        }
    }

    private boolean hasCircularDependencies(Set<EntityWrapper> wrappers) {
        Map<String, Set<String>> nodes = wrappers.stream().collect(Collectors.toMap(EntityWrapper::getEntityName, EntityWrapper::getDependencies));
        return circularDependencyChecker.hasCircular(nodes);
    }
}
