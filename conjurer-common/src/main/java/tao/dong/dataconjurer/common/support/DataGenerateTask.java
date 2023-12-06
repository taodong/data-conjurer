package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import tao.dong.dataconjurer.common.model.EntityProcessResult;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.LinkedTypedValue;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.ReferenceStrategy;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.INDEX;
import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.MISC;
import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.REFERENCE;

@Builder
@Slf4j
public class DataGenerateTask implements Callable<EntityProcessResult> {

    private final EntityWrapper entityWrapper;
    private final CountDownLatch countDownLatch;
    private final DataGenerateConfig config;
    @Builder.Default
    @Getter
    private Map<Reference, TypedValue> referenced = new HashMap<>();
    @Builder.Default
    private Map<Reference, Boolean> referenceReady = new HashMap<>();

    @Override
    public EntityProcessResult call() {
        Thread.currentThread().setName("data-gen-" + entityWrapper.getId().getIdString());
        try {
            entityWrapper.updateStatus(1);
            generateData();
            entityWrapper.updateStatus(2);
            return new EntityProcessResult(entityWrapper.getId(), entityWrapper.getStatus());
        } catch (DataGenerateException dge) {
            throw dge;
        } catch (Exception e) {
            throw new DataGenerateException(MISC, String.format("Failed to generate data for %s", entityWrapper.getId()), entityWrapper.getId().getIdString(), e);
        } finally {
            countDownLatch.countDown();
        }
    }

    private void generateData() {
        LOG.info("Generating data for entity {}", entityWrapper.getId());
        Map<String, IndexValueGenerator> referenceIndexTracker = new HashMap<>();
        var deferredProperties = getDeferredProperties();

        var recordNum = 0L;
        var collision = 0;
        while (recordNum < entityWrapper.getCount() && collision < config.getMaxIndexCollision()) {
            var dataRow = generateRecord(referenceIndexTracker, deferredProperties, recordNum);
            collision = generateDeferredProperties(dataRow, referenceIndexTracker, deferredProperties, collision);
            if (isValidRecord(dataRow)) {
                entityWrapper.getValues().add(dataRow);
                recordNum++;
            } else {
                collision++;
                LOG.warn("Index collision occurs for generated {} record. record number: {} collision number: {}",
                         entityWrapper.getId(), recordNum, collision);
            }
        }

        if (collision >= config.getMaxIndexCollision() - 1 && !config.isPartialResult()) {
            throw new DataGenerateException(INDEX,
                                            String.format("Failed to generate unique index for %s. Collision number: %d, row number: %d",
                                                          entityWrapper.getId(), collision, recordNum),
                                            entityWrapper.getId().getIdString()
            );
        }
    }

    int generateDeferredProperties(List<Object> dataRow, Map<String, IndexValueGenerator> referenceIndexTracker,
                                    List<String> deferredProperties, int collision) {
        boolean generated = false;
        var propVals = new HashMap<String, Object>();
        if (!deferredProperties.isEmpty()) {
            while (!generated && collision < config.getMaxIndexCollision()) {
                var pillars = new HashMap<String, LinkedPair>();
                propVals.clear();
                try {
                    for (var propertyName : deferredProperties) {
                        var reference = entityWrapper.getReferences().get(propertyName);
                        var linkedProp = reference.entity() + '_' + reference.linked();
                        if (!pillars.containsKey(linkedProp)) {
                            var pillarVal = getPillarValue(referenceIndexTracker, propertyName, linkedProp, reference);
                            pillars.put(linkedProp, pillarVal);
                            propVals.put(propertyName, pillarVal.value());
                        } else {
                            var key = pillars.get(linkedProp).key();
                            var val = getLinkedValue(referenceIndexTracker, propertyName, reference, key);
                            propVals.put(propertyName, val);
                        }
                    }

                    if (propVals.size() == deferredProperties.size()) {
                        generated = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    LOG.warn("Failed to generate deferred property value");
                    collision++;
                }
            }

            if (generated) {
                for (var propEntry : propVals.entrySet()) {
                    dataRow.set(entityWrapper.getPropertyOrders().get(propEntry.getKey()), propEntry.getValue());
                }
            }
        }

        return collision;
    }

    private Object getLinkedValue(Map<String, IndexValueGenerator> referenceIndexTracker, String propertyName, Reference reference, String key) {
        var typedValue = (LinkedTypedValue)referenced.get(reference);
        var strategy = entityWrapper.getRefStrategy().get(propertyName);
        var size = typedValue.getKeyedValues(key).size();
        var indexGen = getIndexValueGenerator(referenceIndexTracker, propertyName + ':' + key, strategy, typedValue.getKeyedValues(key).size());
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(0, Math.min(size, 15)); i++) {
            indexGen.generate(); // shuffle values
        }
        return typedValue.getKeyedValues(key).get(indexGen.generate());
    }

    private LinkedPair getPillarValue(Map<String, IndexValueGenerator> referenceIndexTracker, String propertyName, String linkedProp, Reference reference) {
        var typedValue = (LinkedTypedValue)referenced.get(reference);
        var strategy = entityWrapper.getRefStrategy().get(propertyName);
        var keyIndexGen = getIndexValueGenerator(referenceIndexTracker, propertyName + '_' + linkedProp, strategy, typedValue.getOrderedKeys().size());
        var key = typedValue.getOrderedKeys().get(keyIndexGen.generate());
        var valIndexGen = getIndexValueGenerator(referenceIndexTracker, propertyName + ':' + key, strategy, typedValue.getKeyedValues(key).size());
        var val= typedValue.getKeyedValues(key).get(valIndexGen.generate());
        return new LinkedPair(key, val);
    }

    List<String> getDeferredProperties() {
        return entityWrapper.getReferences().entrySet().stream().filter(entry -> entry.getValue().linked() != null)
                .map(Map.Entry::getKey).distinct().toList();
    }

    private boolean isValidRecord(List<Object> dataRow) {
        var index = 0;
        var valid = true;
        for (var indexedValue : entityWrapper.getIndexes()) {
            valid = indexedValue.addValue(dataRow);
            if (!valid) {
                break;
            }
            index++;
        }

        if (!valid && index > 0) {
            // Rollback inserted index values
            for (var i = 0; i < index; i++) {
                entityWrapper.getIndexes().get(i).removeLastValue();
            }
        }
        return valid;
    }

    private List<Object> generateRecord(Map<String, IndexValueGenerator> referenceIndexTracker, List<String> deferredProperties, long recordNum) {

        List<Object> dataRow = new ArrayList<>(entityWrapper.getProperties().size());
        for (String propertyName : entityWrapper.getProperties()) {
            Object val;
            if (entityWrapper.getEntries().containsKey(propertyName) && entityWrapper.getEntries().get(propertyName).size() >= recordNum) {
              val = entityWrapper.getEntries().get(propertyName).get((int)recordNum);
            } else if (entityWrapper.getReferences().containsKey(propertyName)) {
                var reference = entityWrapper.getReferences().get(propertyName);
                var ready = referenceReady.computeIfAbsent(reference, this::isReferenceReady);
                if (Boolean.FALSE.equals(ready)) {
                    throw new DataGenerateException(REFERENCE,
                                                    String.format("No reference is available for %s.%s", entityWrapper.getId().entityName(), propertyName), entityWrapper.getId().getIdString());
                }
                if (!deferredProperties.contains(propertyName)) {
                    val = retrieveReferencedValue(referenceIndexTracker, propertyName, reference);
                } else {
                    val = null;
                }
            } else {
                val = entityWrapper.getGenerators().get(propertyName).generate();
            }
//            if (entityWrapper.getReferenced().containsKey(propertyName)) {
//                ((SimpleTypedValue)entityWrapper.getReferenced().get(propertyName)).addValue(val); // TODO: ...
//            }
            dataRow.add(val);
        }

        return dataRow;
    }

    private Object retrieveReferencedValue(Map<String, IndexValueGenerator> referenceIndexTracker, String propertyName, Reference reference) {
        var indexGen = getIndexValueGenerator(referenceIndexTracker, propertyName, entityWrapper.getRefStrategy().get(propertyName), referenced.get(reference).getOrderedValues().size());
        return referenced.get(reference).getOrderedValues().get(indexGen.generate());
    }

    private IndexValueGenerator getIndexValueGenerator(Map<String, IndexValueGenerator> referenceIndexTracker, String key, String strategy, int maxIndex) {
        return referenceIndexTracker.computeIfAbsent(key, k -> createReferenceIndexGenerator(strategy, maxIndex));
    }

    protected IndexValueGenerator createReferenceIndexGenerator(String strategy, int size) {
        var refStrategy = ReferenceStrategy.getByName(strategy);
        return switch (refStrategy) {
            case RANDOM -> new RandomIndexGenerator(size);
            case LOOP -> new LoopIndexGenerator(size);
        };
    }

    private boolean isReferenceReady(Reference key) {
        return CollectionUtils.isNotEmpty(referenced.get(key).getOrderedValues());
    }

    record LinkedPair(String key, Object value){}

}
