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
        var entityMap = createEntityMapWithReference(entities);
        generateEntityData(entityMap);
    }

    Map<String, EntityWrapper> createEntityMapWithReference(Set<EntityWrapper> entities) {
        var entityMap = new HashMap<String, EntityWrapper>();
        var refs = new HashMap<String, Set<String>>();
        for (var entity : entities) {
            entityMap.put(entity.getEntityName(), entity);
            for (var ref : entity.getReferences().values()) {
                refs.compute(ref.entity(), (k, v) -> {
                    if (v == null) {
                        v = new HashSet<>();
                    }
                    v.add(ref.property());
                    return v;
                });
            }
        }
        for (var entry : refs.entrySet()) {
            entityMap.get(entry.getKey()).createReferenced(entry.getValue().toArray(String[]::new));
        }
        return entityMap;
    }

    private void generateEntityData(Map<String, EntityWrapper> entityMap) {
        var pool = Executors.newFixedThreadPool(config.getHandlerCount());
        try {
            var processed = 0;
            var timeout = System.currentTimeMillis() + config.getDataGenTimeOut().toMillis();
            while (processed <= entityMap.size() - 1 && System.currentTimeMillis() < timeout) {
                var runners = findReadyEntities(entityMap);
                var candidateSize = runners.size();
                removeDropouts(runners, entityMap);
                processed += (candidateSize - runners.size());
                if (!runners.isEmpty()) {
                    var cleanup = false;
                    try {
                        final var latch = new CountDownLatch(runners.size());
                        var futures = runners.stream()
                                        .map(target -> createDataGenerateTask(target, entityMap, latch))
                                        .map(pool::submit)
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
        } finally {
            pool.shutdown();
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

   DataGenerateTask createDataGenerateTask(EntityWrapper target, Map<String, EntityWrapper> data, CountDownLatch latch) {
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

    Set<EntityWrapper> findReadyEntities(Map<String, EntityWrapper> entityMap) {
        Set<EntityWrapper> candidates = new HashSet<>();
        for (var wrapper : entityMap.values()) {
            if (wrapper.getStatus() == 0 && wrapper.getDependencies().stream().map(entityMap::get).noneMatch(en -> en.getStatus() != -1 && en.getStatus() != 2)) {
                candidates.add(wrapper);
            }
        }
        return candidates;
    }

    void removeDropouts(Set<EntityWrapper> entityWrappers, Map<String, EntityWrapper> entityMap) {
        var it = entityWrappers.iterator();
        while (it.hasNext()) {
            var cur = it.next();
            if (cur.getDependencies().stream().map(entityMap::get).anyMatch(en -> en.getStatus() == -1)) {
                entityMap.get(cur.getEntityName()).failProcess("Dependency generation failed");
                it.remove();
            }
        }
    }


    void validate(Set<EntityWrapper> wrappers) {
        if (hasCircularDependencies(wrappers)) {
            throw new DataGenerateException(DEPENDENCE, "Circular dependencies found among entities");
        }
    }

    private boolean hasCircularDependencies(Set<EntityWrapper> wrappers) {
        Map<String, Set<String>> nodes = wrappers.stream().collect(Collectors.toMap(EntityWrapper::getEntityName, EntityWrapper::getDependencies));
        return circularDependencyChecker.hasCircular(nodes);
    }
}
