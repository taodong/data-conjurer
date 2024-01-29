package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.support.DataGenerateException;
import tao.dong.dataconjurer.common.support.DataGenerationErrorType;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.DefaultStringPropertyValueConverter;
import tao.dong.dataconjurer.common.support.DeferredCompoundValueGenerator;
import tao.dong.dataconjurer.common.support.ElectedValueSelector;
import tao.dong.dataconjurer.common.support.NumberCalculator;
import tao.dong.dataconjurer.common.support.PropertyCategorySupplier;
import tao.dong.dataconjurer.common.support.StringPropertyValueConverter;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;
import tao.dong.dataconjurer.common.support.WeightedValueGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
public class EntityWrapper {
    private final EntityWrapperId id;
    private final DataEntity entity;
    private final Set<String> dependencies = new HashSet<>();
    /**
     * status - entity process status
     *  0 - unprocessed
     *  1 - in process
     *  2 - completed
     *  -1 - completed with error
     */
    private final AtomicInteger status = new AtomicInteger(0);
    private final long count;
    private final int bufferSize;

    private final Map<String, TypedValue> referenced = new HashMap<>();
    private final Map<String, Reference> references = new HashMap<>();
    private final List<List<Object>> values = new ArrayList<>();
    private final Map<String, ValueGenerator<?>> generators = new HashMap<>();
    private final List<String> properties = new ArrayList<>();
    private final List<PropertyType> propertyTypes = new ArrayList<>();
    private final List<UniqueIndex<?>> indexes = new ArrayList<>();
    private final Set<Integer> hiddenIndex = new HashSet<>();
    private final Map<String, String> aliases = new HashMap<>();
    private final Map<String, String> refStrategy = new HashMap<>();
    private final Map<String, List<Object>> entries = new HashMap<>();
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Integer> propertyOrders = new HashMap<>();
    private final Set<String> correlated = new HashSet<>();
    private final Map<String, DeferredCompoundValueGenerator> compoundValueGenerators = new HashMap<>();
    private final Map<String, Integer> provided = new HashMap<>();
    private String msg;

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, PropertyType> typeMap = new HashMap<>();
    protected final DataProviderService dataProviderService;

    public EntityWrapper(@NotNull DataEntity entity, @NotNull EntityData data, EntityOutputControl outputControl, DataProviderService dataProviderService, int bufferSize) {
        this.dataProviderService = dataProviderService;
        this.entity = entity;
        this.count = data.count();
        this.bufferSize = bufferSize;
        this.id = new EntityWrapperId(entity.name(), data.dataId());
        var indexedProps = new HashMap<Integer, Map<Integer, EntityIndex>>();
        var hiddenProps = new HashSet<String>();
        var propInputs = DataHelper.streamNullableCollection(data.properties()).collect(Collectors.toMap(PropertyInputControl::name, Function.identity()));

        if (outputControl != null) {
            processOutputControl(outputControl, hiddenProps);
        }
        processProperties(indexedProps, hiddenProps, propInputs);
        if (data.entries() != null && CollectionUtils.isNotEmpty(data.entries().values())) {
            saveEntries(data.entries());
        }
        createIndexes(indexedProps);
    }

    void saveEntries(@NotNull EntityEntry entry) {
        var propertyValueConverter = new DefaultStringPropertyValueConverter();

        var ps = entry.properties();
        var vals = entry.values();

        for (var rc : vals) {
            var converted = convertEntryVal(propertyValueConverter, ps, rc);
            var error = converted.stream().filter(v -> v instanceof ConvertError).findFirst();
            if (error.isPresent()) {
                LOG.warn("Failed to process entity entry value, record skipped: " + ((ConvertError)(error.get())).message());
                break;
            }
            for (var i = 0; i < converted.size(); i++) {
                entries.computeIfAbsent(ps.get(i), k -> new ArrayList<>()).add(converted.get(i));
            }
        }
    }

    List<Object> convertEntryVal(StringPropertyValueConverter<Object> converter, List<String> props, List<String> rc) {
        return IntStream.range(0, props.size()).mapToObj(i -> converter.convert(rc.get(i), typeMap.get(props.get(i)))).toList();
    }

    void createIndexes(Map<Integer, Map<Integer, EntityIndex>> indexedProps) {
        if (!indexedProps.isEmpty()) {
            for (var indexDefs : indexedProps.values()) {
                indexes.add(createIndex(indexDefs));
            }
        }
    }

    UniqueIndex<?> createIndex(Map<Integer, EntityIndex> indexDefs) {
        boolean first = true;
        int type = 0;
        int parent = -1;
        int child = -1;
        var ids = new ArrayList<Integer>();
        List<Integer> distinctEls = new ArrayList<>();
        for (var entry : indexDefs.entrySet()) {
            var propIndex = entry.getKey();
            ids.add(propIndex);
            var def = entry.getValue();
            if (first) {
                type = def.type();
                first = false;
            } else if (type != def.type()) {
                throw new DataGenerateException(DataGenerationErrorType.INDEX,
                        "Multiple index types defined under the same id %d in %s".formatted(def.id(), getEntityName()));
            }

            if ((type == 2 || type == 3) && def.qualifier() == 1) {
                parent = propIndex;
            } else if ((type == 2 || type == 3) && def.qualifier() == 2) {
                child = propIndex;
            } else if (type == 4 && def.qualifier() == 1) {
                distinctEls.add(propIndex);
            }
        }

        if (type == 2 && (parent < 0 || child < 0)) {
            throw new DataGenerateException(DataGenerationErrorType.INDEX,
                    "Missing qualifier for type 2 index under in %s".formatted(getEntityName()));
        }

        Function<List<Integer>, int[]> convertToArray = list -> list.stream().mapToInt(x -> x).toArray();
        var idArr = convertToArray.apply(ids);
        return switch (type) {
            case 1 -> new UnorderedIndexedValue(idArr);
            case 2 -> new NonCircleIndexValue(idArr, parent, child, false);
            case 3 -> new NonCircleIndexValue(idArr, parent, child, true);
            case 4 -> new DistinctElementIndexedValue(idArr, convertToArray.apply(distinctEls));
            default -> new IndexedValue(convertToArray.apply(ids));
        };

    }

    void processOutputControl(EntityOutputControl outputControl, Set<String> hiddenProps) {
        for (var propControl : outputControl.properties()) {
            if (StringUtils.isNotBlank(propControl.alias())) {
                aliases.put(propControl.name(), propControl.alias());
            }

            if (propControl.hide()) {
                hiddenProps.add(propControl.name());
            }
        }
     }

    void processProperties(Map<Integer, Map<Integer, EntityIndex>> indexedProps, Set<String> hiddenProps, Map<String, PropertyInputControl> propInputs) {
        var propIndex = 0;

        for (var property : entity.properties()) {
            // List reference
            if (property.reference() != null) {
                dependencies.add(property.reference().entity());
                references.put(property.name(), property.reference());
            }
            // Extract indexed properties
            if (property.index() != null) {
                indexedProps.computeIfAbsent(property.index().id(), k -> new HashMap<>()).put(propIndex, property.index());
            }
            // Populate hidden index
            if (hiddenProps.contains(property.name())) {
                hiddenIndex.add(propIndex);
            }

            // Record reference strategy
            var inputControl = propInputs.get(property.name());
            if (inputControl != null && inputControl.referenceStrategy() != null) {
                refStrategy.put(property.name(), inputControl.referenceStrategy());
            }

            // Create generators
            properties.add(property.name());
            propertyTypes.add(property.type());
            typeMap.put(property.name(), property.type());
            generators.put(property.name(), matchValueGenerator(property, inputControl));
            propertyOrders.put(property.name(), propIndex);
            propIndex++;
        }
    }

    protected ValueGenerator<?> matchValueGenerator(EntityProperty property, PropertyInputControl input) {
        if (input != null && CollectionUtils.isNotEmpty(input.constraints())) {
            property = property.addConstraints(input.constraints());
        }

        var firstChoiceGenerator = getPossibleDataProviderBackedGenerator(property);
        if (firstChoiceGenerator != null) {
            return firstChoiceGenerator;
        }

        var fallbackGenerator = matchFallbackValueGenerator(property);
        if (input != null) {
            var propertyValueConverter = new DefaultStringPropertyValueConverter();
            var providedValues = new ArrayList<WeightedValue>();
            if (input.values() != null) {
                var currentMin = 0.0;
                for (var dist : input.values()) {
                    var currentMax = currentMin + dist.weight();
                    var converted = propertyValueConverter.convertValues(dist.values(), property.type());
                    if (!converted.isEmpty()) {
                        providedValues.add(
                                new WeightedValue(
                                        new ElectedValueSelector(new HashSet<>(converted)),
                                        new RatioRange(currentMin, currentMax)
                                )
                        );
                        currentMin = currentMax;
                    } else {
                        LOG.warn("Failed to parse any given value. Skip weighted value generation for {}.{} with weight {}", getEntityName(), property.name(), dist.weight());
                    }
                }
            }
            if (input.defaultValue() != null) {
                var converted = propertyValueConverter.convert(input.defaultValue(), property.type());
                if (converted instanceof ConvertError ce) {
                    LOG.warn("Failed to parse default value {} for {}.{}. Ignored. {}", input.defaultValue(), getEntityName(), property.name(), ce.message());
                } else {
                    fallbackGenerator = new ElectedValueSelector(Set.of(converted));
                }
            }
            if (!providedValues.isEmpty()) {
                return new WeightedValueGenerator(property.type(), providedValues, fallbackGenerator);
            }
        }

        if (fallbackGenerator instanceof NumberCalculator) {
            correlated.add(property.name());
        }

        return fallbackGenerator;
    }

    private Optional<ValueCategory> lookupValueCategoryConstraint(EntityProperty property) {
        if (property.type() == PropertyType.TEXT) {
            return DataHelper.streamNullableCollection(property.constraints())
                    .filter(c -> c instanceof ValueCategory)
                    .map(ValueCategory.class::cast)
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    protected ValueGenerator<?> getPossibleDataProviderBackedGenerator(EntityProperty property) {
        try {
            var valueCategory = lookupValueCategoryConstraint(property);
            if (valueCategory.isPresent()) {
                var vc = valueCategory.get();
                var dataProvider = dataProviderService.getValueProviderByType(vc.name());
                var deferredGenerator = compoundValueGenerators.computeIfAbsent(vc.getValueId(),
                        k -> new DeferredCompoundValueGenerator(dataProvider, Math.toIntExact(count) + bufferSize, vc.locale())
                );

                deferredGenerator.addConstraints(vc.qualifier(),
                        property.constraints().stream()
                                .filter(constraint -> constraint.getType() != ConstraintType.CATEGORY)
                                .toList()
                );

                provided.put(property.name(), vc.compoundId());
                return new PropertyCategorySupplier(vc);
            }
        } catch (Exception e) {
            LOG.warn("Failed to create generator for Value Category of {}.{}", getEntityName(), property.name(), e);
        }
        return null;
    }

    protected ValueGenerator<?> matchFallbackValueGenerator(EntityProperty property) {
        var typedValueGenerator = new TypedValueGenerator() {
            @Override
            public ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property, DataProviderService dataProviderService) {
                return TypedValueGenerator.super.matchDefaultGeneratorByType(property, dataProviderService);
            }
        };
        return typedValueGenerator.matchDefaultGeneratorByType(property, dataProviderService);
    }

    public int getStatus() {
        return status.get();
    }

    public void updateStatus(int targetStatus) {
        if (targetStatus - 1 >= 0) {
            status.compareAndSet(targetStatus - 1, targetStatus);
        }
    }

    public void failProcess(String message) {
        if (status.get() != 2) {
            msg = message;
            status.set(-1);
        }
    }

    public void createReferenced(PropertyLink... properties) {
        if (properties != null) {
            for (var prop : properties) {
                referenced.compute(prop.name(), (k, v) -> computeTypedValue(prop, v));
            }
        }
    }

    TypedValue computeTypedValue(PropertyLink propertyLink, TypedValue currentValue) {
        if (currentValue == null || (currentValue.getDataType() == TypedValue.DataType.SIMPLE && propertyLink.linked() != null)) {
            return createReferenceTypedValue(propertyLink);
        } else{
            return currentValue;
        }
    }

    private TypedValue createReferenceTypedValue(PropertyLink prop) {
        validatePropertyLink(prop);
        var type = typeMap.get(prop.name());
        return prop.linked() == null ? new SimpleTypedValue(type) : new LinkedTypedValue(type, prop.linked());
    }

    void validatePropertyLink(PropertyLink propertyLink) {
        if (!typeMap.containsKey(propertyLink.name()) || (propertyLink.linked() != null && !typeMap.containsKey(propertyLink.linked()))) {
            throw new IllegalArgumentException("Property " + propertyLink + " isn't defined in " + id.entityName());
        }
    }

    public Map<Reference, TypedValue> getReferencedByProperties(String... properties) {
        Map<Reference, TypedValue> rs = new HashMap<>();
        if (properties != null) {
            for (var prop : properties) {
                if (referenced.containsKey(prop)) {
                    var tv = referenced.get(prop);
                    String linked = null;
                    if (tv instanceof LinkedTypedValue ltv) {
                        linked = ltv.getLinked();
                    }
                    rs.put(new Reference(id.entityName(), prop, linked), tv);
                }
            }
        }
        return rs;
    }

    public String getEntityName() {
        return id.entityName();
    }

    public List<PropertyType> getOutputPropertyTypes() {
        if (!hiddenIndex.isEmpty()) {
            return DataHelper.removeIndexFromList(propertyTypes, hiddenIndex);
        } else {
            return propertyTypes;
        }
    }

    public List<String> getOutputProperties() {
        List<String> props = new ArrayList<>(properties);
        if (!hiddenIndex.isEmpty()) {
            props = DataHelper.removeIndexFromList(props, hiddenIndex);
        }
        if (!aliases.isEmpty()) {
            props = props.stream()
                    .map(name -> aliases.getOrDefault(name, name))
                    .toList();
        }
        return props;
    }

    public List<List<Object>> getOutputValues() {
        return values.stream()
                .map(row -> DataHelper.removeIndexFromList(row, hiddenIndex))
                .toList();
    }

    public int getPropertyOrder(String propertyName) {
        return propertyOrders.getOrDefault(propertyName, -1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityWrapper other) {
            return id.equals(other.getId());
        }
        return false;
    }
}
