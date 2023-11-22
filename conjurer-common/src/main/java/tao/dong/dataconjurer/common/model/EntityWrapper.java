package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.DefaultStringPropertyValueConverter;
import tao.dong.dataconjurer.common.support.ElectedValueSelector;
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

    private final Map<String, TypedValue> referenced = new HashMap<>();
    private final Map<String, Reference> references = new HashMap<>();
    private final List<List<Object>> values = new ArrayList<>();
    private final Map<String, ValueGenerator<?>> generators = new HashMap<>();
    private final List<String> properties = new ArrayList<>();
    private final List<PropertyType> propertyTypes = new ArrayList<>();
    private final List<IndexedValue> indexes = new ArrayList<>();
    private final Set<Integer> hiddenIndex = new HashSet<>();
    private final Map<String, String> aliases = new HashMap<>();
    private final Map<String, String> refStrategy = new HashMap<>();
    private String msg;

    public EntityWrapper(@NotNull DataEntity entity, @NotNull EntityData data) {
        this(entity, data, null);
    }

    public EntityWrapper(@NotNull DataEntity entity, @NotNull EntityData data, EntityOutputControl outputControl) {
        this.entity = entity;
        this.count = data.count();
        this.id = new EntityWrapperId(entity.name(), data.dataId());
        var indexedProps = new HashMap<Integer, Set<Integer>>();
        var hiddenProps = new HashSet<String>();
        var propInputs = DataHelper.streamNullableCollection(data.properties()).collect(Collectors.toMap(PropertyInputControl::name, Function.identity()));

        if (outputControl != null) {
            processOutputControl(outputControl, hiddenProps);
        }
        processProperties(indexedProps, hiddenProps, propInputs);
        createIndex(indexedProps);
    }

    void createIndex(Map<Integer, Set<Integer>> indexedProps) {
        if (!indexedProps.isEmpty()) {
            for (var ii : indexedProps.values()) {
                indexes.add(new IndexedValue(
                        ii.stream().mapToInt(Integer::intValue).toArray()
                ));
            }
        }
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

    void processProperties(Map<Integer, Set<Integer>> indexedProps, Set<String> hiddenProps, Map<String, PropertyInputControl> propInputs) {
        var propIndex = 0;

        for (var property : entity.properties()) {
            // List reference
            if (property.reference() != null) {
                dependencies.add(property.reference().entity());
                references.put(property.name(), property.reference());
            }
            // Extract indexed properties
            if (property.idIndex() > 0) {
                DataHelper.appendToSetValueInMap(indexedProps, property.idIndex(), propIndex);
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
            generators.put(property.name(), matchValueGenerator(property, inputControl));
            propIndex++;
        }
    }

    protected ValueGenerator<?> matchValueGenerator(EntityProperty property, PropertyInputControl input) {
        var fallbackGenerator = matchFallbackValueGenerator(property);
        if (input != null) {
            var propertyValueConverter = new DefaultStringPropertyValueConverter();
            var providedValues = new ArrayList<WeightedValue>();
            if (input.values() != null) {
                var currentMin = 0.0;
                for (var dist : input.values()) {
                    var currentMax = currentMin + dist.weight();
                    providedValues.add(
                            new WeightedValue(
                                    new ElectedValueSelector(
                                            new HashSet<>(propertyValueConverter.convertValues(dist.values(), property.type()))
                                    ),
                                    new RatioRange(currentMin, currentMax)
                            )
                    );
                    currentMin = currentMax;
                }
            }
            if (input.defaultValue() != null) {
                fallbackGenerator = new ElectedValueSelector(Set.of(propertyValueConverter.convert(input.defaultValue(), property.type())));
            }
            if (!providedValues.isEmpty()) {
                return new WeightedValueGenerator(property.type(), providedValues, fallbackGenerator);
            }
        }
        return fallbackGenerator;
    }

    protected ValueGenerator<?> matchFallbackValueGenerator(EntityProperty property) {
        var typedValueGenerator = new TypedValueGenerator() {
            @Override
            public ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
                return TypedValueGenerator.super.matchDefaultGeneratorByType(property);
            }
        };
        return typedValueGenerator.matchDefaultGeneratorByType(property);
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

    public void createReferenced(String... properties) {
        if (properties != null) {
            for (var prop : properties) {
                referenced.computeIfAbsent(prop, this::createReferenceTypedValue);
            }
        }
    }

    private TypedValue createReferenceTypedValue(String propName) {
        return entity.properties().stream().filter(property -> StringUtils.equals(propName, property.name()))
                .findFirst().map(p -> new TypedValue(p.type())).orElseThrow(() -> new IllegalArgumentException("Property " + propName + " isn't defined in " + id.entityName()));
    }

    public Map<Reference, TypedValue> getReferencedByProperties(String... properties) {
        Map<Reference, TypedValue> rs = new HashMap<>();
        if (properties != null) {
            for (var prop : properties) {
                if (referenced.containsKey(prop)) {
                    rs.put(new Reference(id.entityName(), prop), referenced.get(prop));
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityWrapper other) {
            return id.equals(other.getId());
        }
        return false;
    }
}
