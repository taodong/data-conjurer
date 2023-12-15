package tao.dong.dataconjurer.common.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.TypedValue;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataGenerateTask;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class DataGenerateService {

    public void generateData(@NotNull DataBlueprint blueprint, @NotNull DataGenerateConfig config) {
        generateEntityData(blueprint, config);
    }

    private void generateEntityData(DataBlueprint blueprint, DataGenerateConfig config) {
        var entityMap = blueprint.getEntities();
        var entityIdMap = blueprint.getEntityWrapperIds();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var processed = 0;
            var timeout = System.currentTimeMillis() + config.getDataGenTimeOut().toMillis();
            while (processed <= entityMap.size() - 1 && System.currentTimeMillis() < timeout) {
                var runners = findReadyEntities(entityMap, entityIdMap);
                var candidateSize = runners.size();
                removeDropouts(runners, entityMap, entityIdMap);
                processed += (candidateSize - runners.size());
                if (!runners.isEmpty()) {
                    var cleanup = false;
                    try {
                        final var latch = new CountDownLatch(runners.size());
                        var futures = runners.stream()
                                        .map(target -> createDataGenerateTask(target, entityMap, entityIdMap, latch, config))
                                        .map(executor::submit)
                                        .toList();

                        var lrs = latch.await(config.getEntityGenTimeOut().getSeconds(), TimeUnit.SECONDS);
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
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(config.getDataGenCheckInterval().getSeconds());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (processed < entityMap.size()) {
                failDataGeneration(entityMap.values(), "Data generation timeout after " + config.getDataGenTimeOut().toSeconds() + " seconds.");
            }
        }
    }

    void failDataGeneration(Collection<EntityWrapper> wrappers, String message) {
        for (var wrapper : wrappers) {
            failEntityGeneration(wrapper, message);
        }
    }

    private void failEntityGeneration(EntityWrapper wrapper, String message) {
        if (wrapper.getStatus() != 2 && wrapper.getStatus() != -1) {
            wrapper.failProcess(message);
        }
    }

   DataGenerateTask createDataGenerateTask(EntityWrapper target, Map<EntityWrapperId, EntityWrapper> data,
                                           Map<String, Set<EntityWrapperId>> entityIdMap, CountDownLatch latch, DataGenerateConfig config) {
        var referenceValues = getReferencedValues(target, data, entityIdMap);
        return DataGenerateTask.builder()
                .config(config)
                .countDownLatch(latch)
                .entityWrapper(target)
                .referenced(referenceValues)
                .build();
    }

    private Map<Reference, TypedValue> getReferencedValues(EntityWrapper target, Map<EntityWrapperId, EntityWrapper> data,
                                                           Map<String, Set<EntityWrapperId>> entityIdMap) {
        Map<Reference, TypedValue> referencedValues = new HashMap<>();
        Map<String, Set<String>> references = new HashMap<>();
        for (var ref : target.getReferences().values()) {
            DataHelper.appendToSetValueInMap(references, ref.entity(), ref.property());
        }
        if (!references.isEmpty()) {
            for (var entry : references.entrySet()) {
                var entities = findWrapperWithEntityName(entry.getKey(), data, entityIdMap);
                for (var entity : entities) {
                    var reffed = entity.getReferencedByProperties(entry.getValue().toArray(String[]::new));
                    joinReferencedValues(referencedValues, reffed);
                }
            }
        }
        return referencedValues;
    }

    void joinReferencedValues(Map<Reference, TypedValue> referencedValues, Map<Reference, TypedValue> toJoin) {
        for (var entry : toJoin.entrySet()) {
            var newValue = entry.getValue();
            if (referencedValues.containsKey(entry.getKey())) {
                referencedValues.get(entry.getKey()).join(newValue);
            } else {
                referencedValues.put(entry.getKey(), newValue);
            }
        }
    }

    Set<EntityWrapper> findReadyEntities(Map<EntityWrapperId, EntityWrapper> entityMap, Map<String, Set<EntityWrapperId>> entityIdMap) {
        Set<EntityWrapper> candidates = new HashSet<>();
        for (var wrapper : entityMap.values()) {
            if (wrapper.getStatus() == 0 &&
                    wrapper.getDependencies().stream().map(key -> findWrapperWithEntityName(key, entityMap, entityIdMap)).flatMap(Set::stream).noneMatch(en -> en.getStatus() != -1 && en.getStatus() != 2)) {
                candidates.add(wrapper);
            }
        }
        return candidates;
    }

    private Set<EntityWrapper> findWrapperWithEntityName(String entityName, Map<EntityWrapperId, EntityWrapper> entityMap, Map<String, Set<EntityWrapperId>> entityIdMap) {
        return entityIdMap.computeIfAbsent(entityName, key -> new HashSet<>())
                .stream()
                .map(entityMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    void removeDropouts(Set<EntityWrapper> entityWrappers, Map<EntityWrapperId, EntityWrapper> entityMap, Map<String, Set<EntityWrapperId>> entityIdMap) {
        var it = entityWrappers.iterator();
        while (it.hasNext()) {
            var cur = it.next();
            if (cur.getDependencies().stream().map(key -> findWrapperWithEntityName(key, entityMap, entityIdMap)).flatMap(Set::stream).anyMatch(en -> en.getStatus() == -1)) {
                entityMap.get(cur.getId()).failProcess("Dependency generation failed");
                it.remove();
            }
        }
    }
}
