package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

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
    private String msg;
    protected TypedValueGenerator typedValueGenerator = new TypedValueGenerator() {
        @Override
        public ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
            return TypedValueGenerator.super.matchDefaultGeneratorByType(property);
        }
    };

    public EntityWrapper(@NotNull DataEntity entity, @NotNull EntityData data) {
        this(entity, data, null);
    }

    public EntityWrapper(@NotNull DataEntity entity, @NotNull EntityData data, EntityOutputControl outputControl) {
        this.entity = entity;
        this.count = data.count();
        this.id = new EntityWrapperId(entity.name(), data.dataId());
        var indexedProps = new HashMap<Integer, Set<Integer>>();

        processProperties(indexedProps);

        // Create indexes
        if (!indexedProps.isEmpty()) {
            for (var ii : indexedProps.values()) {
                indexes.add(new IndexedValue(
                        ii.stream().mapToInt(Integer::intValue).toArray()
                ));
            }
        }
    }

    void processProperties(HashMap<Integer, Set<Integer>> indexedProps) {
        var propIndex = 0;

        for (var property : entity.properties()) {
            // List Reference
            if (property.reference() != null) {
                dependencies.add(property.reference().entity());
                references.put(property.name(), property.reference());
            }
            // extract indexed properties
            if (property.idIndex() > 0) {
                DataHelper.appendToSetValueInMap(indexedProps, property.idIndex(), propIndex);
            }

            // Create generators
            properties.add(property.name());
            propertyTypes.add(property.type());
            generators.put(property.name(), matchValueGenerator(property));
            propIndex++;
        }
    }

    protected ValueGenerator<?> matchValueGenerator(EntityProperty property) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityWrapper other) {
            return id.equals(other.getId());
        }
        return false;
    }
}
