package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import tao.dong.dataconjurer.common.model.EntityProcessResult;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.*;

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
        try {
            entityWrapper.updateStatus(1);
            generateData();
            entityWrapper.updateStatus(2);
            return new EntityProcessResult(entityWrapper.getEntityName(), entityWrapper.getStatus());
        } catch (DataGenerateException dge) {
            throw dge;
        } catch (Exception e) {
            throw new DataGenerateException(MISC, String.format("Failed to generate data for %s", entityWrapper.getEntityName()), entityWrapper.getEntityName(), e);
        } finally {
            countDownLatch.countDown();
        }
    }

    private void generateData() {
        Map<String, IndexValueGenerator> referenceIndexTracker = new HashMap<>();
        var recordNum = 0L;
        var collision = 0;
        while (recordNum < entityWrapper.getCount() && collision < config.getMaxIndexCollision()) {
            var dataRow = generateRecord(referenceIndexTracker);
            if (isValidRecord(dataRow)) {
                entityWrapper.getValues().add(dataRow);
                recordNum++;
            } else {
                collision++;
                LOG.warn("Index collision occurs for generated {} record. record number: {} collision number: {}",
                         entityWrapper.getEntityName(), recordNum, collision);
            }
        }

        if (collision >= config.getMaxIndexCollision() - 1 && !config.isPartialResult()) {
            throw new DataGenerateException(INDEX,
                                            String.format("Failed to generate unique index for %s. Collision number: %d, row number: %d",
                                                          entityWrapper.getEntityName(), collision, recordNum),
                                            entityWrapper.getEntityName()
            );
        }
    }

    private boolean isValidRecord(List<Object> dataRow) {
        var index = 0;
        var valid = true;
        for (var indexedValue : entityWrapper.getIndexes()) {
            valid = indexedValue.addValue(dataRow);
            index++;
            if (!valid) {
                break;
            }
        }

        if (!valid) {
            // Rollback inserted index values
            for (var i = 0; i < index; i++) {
                entityWrapper.getIndexes().get(i).removeLastValue();
            }
        }
        return valid;
    }

    private List<Object> generateRecord(Map<String, IndexValueGenerator> referenceIndexTracker) {

        List<Object> dataRow = new ArrayList<>(entityWrapper.getProperties().size());
        for (String propertyName : entityWrapper.getProperties()) {
            Object val;
            if (entityWrapper.getReferences().containsKey(propertyName)) {
                var reference = entityWrapper.getReferences().get(propertyName);
                var ready = referenceReady.computeIfAbsent(reference, this::isReferenceReady);
                if (Boolean.FALSE.equals(ready)) {
                    throw new DataGenerateException(REFERENCE,
                                                    String.format("No reference is available for %s.%s", entityWrapper.getEntityName(), propertyName), entityWrapper.getEntityName());
                }
                var indexGen = referenceIndexTracker.computeIfAbsent(propertyName, k -> new RandomIndexGenerator(referenced.get(reference).getOrderedValues().size()));
                val = referenced.get(reference).getOrderedValues().get(indexGen.generate());
            } else {
                val = entityWrapper.getGenerators().get(propertyName).generate();
            }
            if (entityWrapper.getReferenced().containsKey(propertyName)) {
                entityWrapper.getReferenced().get(propertyName).addValue(val);
            }
            dataRow.add(val);
        }

        return dataRow;
    }

    private boolean isReferenceReady(Reference key) {
        return CollectionUtils.isNotEmpty(referenced.get(key).getOrderedValues());
    }

}
