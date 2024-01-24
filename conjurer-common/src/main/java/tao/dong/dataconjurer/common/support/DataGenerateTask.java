package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.DeferredPropertyType;
import tao.dong.dataconjurer.common.model.EntityProcessResult;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.LinkedTypedValue;
import tao.dong.dataconjurer.common.model.NonCircleIndexValue;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.ReferenceStrategy;
import tao.dong.dataconjurer.common.model.SimpleTypedValue;
import tao.dong.dataconjurer.common.model.TypedValue;
import tao.dong.dataconjurer.common.model.ValueCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Builder.Default
    private CompoundValuePropertyRetriever compoundValuePropertyRetriever = new CompoundValuePropertyRetriever();

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
            try {
                var dataRow = generateRecord(referenceIndexTracker, deferredProperties, recordNum);
                generateDeferredProperties(dataRow, referenceIndexTracker, deferredProperties);
                populateReferencedValues(dataRow);
                if (isValidRecord(dataRow)) {
                    entityWrapper.getValues().add(dataRow);
                    recordNum++;
                } else {
                    collision++;
                    LOG.warn("Index collision occurs for generated {} record. record number: {}, collision number: {}",
                             entityWrapper.getId(), recordNum, collision);
                }
            } catch (ConstraintViolationException e) {
                collision++;
                LOG.warn("Generated value failed constraint for {}. record number: {}, collision number {}", entityWrapper.getId(), recordNum, collision, e);
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

    private void populateReferencedValues(List<Object> dataRow) {
        Function<String, Object> getPropValue = propName -> dataRow.get(entityWrapper.getPropertyOrder(propName));
        for (var entry : entityWrapper.getReferenced().entrySet()) {
            var propertyName = entry.getKey();
            var typedValue = entry.getValue();
            if (typedValue instanceof SimpleTypedValue stv) {
                stv.addValue(getPropValue.apply(propertyName));
            } else if (typedValue instanceof LinkedTypedValue ltv) {
                var linked = ltv.getLinked();
                ltv.addLinkedValue(
                        String.valueOf(getPropValue.apply(linked)),
                        getPropValue.apply(propertyName)
                );
            }
        }
    }

    private void generateDeferredProperties(List<Object> dataRow, Map<String, IndexValueGenerator> referenceIndexTracker,
                                   Map<DeferredPropertyType, Collection<String>> deferredProperties) {
        if (CollectionUtils.isNotEmpty(deferredProperties.get(DeferredPropertyType.INDEX))) {
            generateDeferredIndexProperties(dataRow, referenceIndexTracker, deferredProperties.get(DeferredPropertyType.INDEX));
        }
        if (CollectionUtils.isNotEmpty(deferredProperties.get(DeferredPropertyType.CORRELATION))) {
            generateCorrelatedProperties(dataRow, deferredProperties.get(DeferredPropertyType.CORRELATION));
        }
    }

    private void generateCorrelatedProperties(List<Object> dataRow, Collection<String> properties) {
        for (var property : properties) {
            var generator = (NumberCalculator)entityWrapper.getGenerators().get(property);
            var dependencies = generator.getParameters();
            var params = dependencies.stream().collect(Collectors.toMap(
                    Function.identity(),
                    prop -> dataRow.get(entityWrapper.getPropertyOrder(prop))
            ));
            var val = generator.calculate(params);
            var propOrder = entityWrapper.getPropertyOrder(property);
            switch (entityWrapper.getPropertyTypes().get(propOrder)) {
                case DATETIME, DATE -> dataRow.set(propOrder, val.longValue());
                default -> dataRow.set(propOrder, val);
            }

        }
    }

    private void generateDeferredIndexProperties(List<Object> dataRow, Map<String, IndexValueGenerator> referenceIndexTracker,
                                    Collection<String> deferredProperties) {
        Map<String, Object> propVals = new HashMap<>();
        if (!deferredProperties.isEmpty()) {
            var pillars = new HashMap<String, LinkedPair>();
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
            } catch (IndexOutOfBoundsException e) {
                throw new DataGenerateException(INDEX,
                        "Failed to generate deferred index property value for entity %s".formatted(entityWrapper.getId()), e);
            }

            for (var propEntry : propVals.entrySet()) {
                dataRow.set(entityWrapper.getPropertyOrder(propEntry.getKey()), propEntry.getValue());
            }
        }
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

    private Map<DeferredPropertyType, Collection<String>> getDeferredProperties() {
        Map<DeferredPropertyType, Collection<String>> deferred = new HashMap<>();
        deferred.put(DeferredPropertyType.CORRELATION, entityWrapper.getCorrelated());
        var linked = entityWrapper.getReferences().entrySet().stream().filter(entry -> entry.getValue().linked() != null)
                .map(Map.Entry::getKey).distinct().toList();
        deferred.put(DeferredPropertyType.INDEX, linked);
        return deferred;
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

    private List<Object> generateRecord(Map<String, IndexValueGenerator> referenceIndexTracker, Map<DeferredPropertyType, Collection<String>> deferredProperties, long recordNum) {

        List<Object> dataRow = new ArrayList<>(entityWrapper.getProperties().size());
        var deferred = deferredProperties.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        Map<String, CompoundValue> compoundValues = new HashMap<>();
        for (String propertyName : entityWrapper.getProperties()) {
            Object val;
            if (entityWrapper.getEntries().containsKey(propertyName) && entityWrapper.getEntries().get(propertyName).size() > recordNum) {
              val = entityWrapper.getEntries().get(propertyName).get((int)recordNum);
            } else if (deferred.contains(propertyName)) {
                val = null;
            } else if (entityWrapper.getProvided().containsKey(propertyName)) {
                var cat = (ValueCategory)entityWrapper.getGenerators().get(propertyName).generate();
                var compoundValue = compoundValues.computeIfAbsent(cat.getValueId(), k -> entityWrapper.getCompoundValueGenerators().get(k).generate());
                val = compoundValuePropertyRetriever.getValue(compoundValue, cat.name(), cat.qualifier());
            } else if (entityWrapper.getReferences().containsKey(propertyName)) {
                var reference = entityWrapper.getReferences().get(propertyName);
                var ready = referenceReady.computeIfAbsent(reference, this::isReferenceReady);
                if (Boolean.FALSE.equals(ready)) {
                    throw new DataGenerateException(REFERENCE,
                            String.format("No reference is available for %s.%s", entityWrapper.getId().entityName(), propertyName), entityWrapper.getId().getIdString());
                }
                val = retrieveReferencedValue(referenceIndexTracker, propertyName, reference);
            } else {
                val = entityWrapper.getGenerators().get(propertyName).generate();
            }

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

    @SuppressWarnings("unused")
    public static class DataGenerateTaskBuilder {
        public DataGenerateTaskBuilder compoundConfig(Map<String, Map<String, String>> extraConfig) {
            if (MapUtils.isNotEmpty(extraConfig)) {
                var retriever = new CompoundValuePropertyRetriever();
                retriever.loadCompoundValueConfiguration(extraConfig);
                this.compoundValuePropertyRetriever$value = retriever;
                this.compoundValuePropertyRetriever$set = true;
            }
            return this;
        }
    }

}
