package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.INDEX;
import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.REFERENCE;

@Builder
@Slf4j
public class DataGenerateTask implements Callable<EntityWrapper> {

    private final EntityWrapper entityWrapper;
    private final CountDownLatch countDownLatch;
    private final DataGenerateConfig config;
    @Builder.Default
    private Map<String, List<TypedValue>> references = new HashMap<>();
    @Builder.Default
    private Map<String, Boolean> referenceReady = new HashMap<>();

    @Override
    public EntityWrapper call() {
        try {
            generateData();
            return entityWrapper;
        } finally {
            countDownLatch.countDown();
        }
    }

    private void generateData() {
        Map<String, IndexValueGenerator> referenceIndexTracker = new HashMap<>();
        var recordNum = 0L;
        var collision = 0;
        while (recordNum < entityWrapper.getCount() || collision < config.getMaxIndexCollision()) {
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

        if (collision >= config.getMaxIndexCollision() && !config.isPartialResult()) {
            throw new DataGenerateException(INDEX,
                                            String.format("Failed to generate unique index for %s. Collision number: %d, row number: %d",
                                                          entityWrapper.getEntityName(), collision, recordNum)
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
                var ready = referenceReady.computeIfAbsent(propertyName, this::isReferenceReady);
                if (Boolean.FALSE.equals(ready)) {
                    throw new DataGenerateException(REFERENCE,
                                                    String.format("No reference is available for %s.%s", entityWrapper.getEntityName(), propertyName));
                }
                var indexGen = referenceIndexTracker.computeIfAbsent(propertyName, k -> new RandomIndexGenerator(references.get(k).size()));
                val = references.get(propertyName).get(indexGen.generate());
            } else {
                val = entityWrapper.getGenerators().get(propertyName).generate();
            }
            dataRow.add(val);
        }

        return dataRow;
    }

    private boolean isReferenceReady(String key) {
        return CollectionUtils.isNotEmpty(references.get(key));
    }

}
