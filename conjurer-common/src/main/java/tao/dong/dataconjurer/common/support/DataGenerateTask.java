package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import tao.dong.dataconjurer.common.model.EntityProcessResult;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.ReferenceStrategy;
import tao.dong.dataconjurer.common.model.SimpleTypedValue;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

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
        var recordNum = 0L;
        var collision = 0;
        while (recordNum < entityWrapper.getCount() && collision < config.getMaxIndexCollision()) {
            var dataRow = generateRecord(referenceIndexTracker, recordNum);
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

    private List<Object> generateRecord(Map<String, IndexValueGenerator> referenceIndexTracker, long recordNum) {

        List<Object> dataRow = new ArrayList<>(entityWrapper.getProperties().size());
        for (String propertyName : entityWrapper.getProperties()) {
            Object val;
            if (entityWrapper.getEntries().containsKey(propertyName) && entityWrapper.getEntries().get(propertyName).size() > recordNum) {
              val = entityWrapper.getEntries().get(propertyName).get((int)recordNum);
            } else if (entityWrapper.getReferences().containsKey(propertyName)) {
                var reference = entityWrapper.getReferences().get(propertyName);
                var ready = referenceReady.computeIfAbsent(reference, this::isReferenceReady);
                if (Boolean.FALSE.equals(ready)) {
                    throw new DataGenerateException(REFERENCE,
                                                    String.format("No reference is available for %s.%s", entityWrapper.getId().entityName(), propertyName), entityWrapper.getId().getIdString());
                }
                var indexGen = referenceIndexTracker.computeIfAbsent(propertyName,
                        k -> createReferenceIndexGenerator(entityWrapper.getRefStrategy().get(propertyName), ((SimpleTypedValue)referenced.get(reference)).getOrderedValues().size())); // TODO:...
                val = ((SimpleTypedValue)referenced.get(reference)).getOrderedValues().get(indexGen.generate()); // TODO:...
            } else {
                val = entityWrapper.getGenerators().get(propertyName).generate();
            }
            if (entityWrapper.getReferenced().containsKey(propertyName)) {
                ((SimpleTypedValue)entityWrapper.getReferenced().get(propertyName)).addValue(val); // TODO: ...
            }
            dataRow.add(val);
        }

        return dataRow;
    }

    protected IndexValueGenerator createReferenceIndexGenerator(String strategy, int size) {
        var refStrategy = ReferenceStrategy.getByName(strategy);
        return switch (refStrategy) {
            case RANDOM -> new RandomIndexGenerator(size);
            case LOOP -> new LoopIndexGenerator(size);
        };
    }

    private boolean isReferenceReady(Reference key) {
        return CollectionUtils.isNotEmpty(((SimpleTypedValue)referenced.get(key)).getOrderedValues());
    }

}
