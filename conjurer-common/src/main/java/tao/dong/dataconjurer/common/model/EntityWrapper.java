package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final String entityName;

    private final Map<String, TypedValue> referenced = new HashMap<>();
    private final Map<String, Reference> references = new HashMap<>();
    private final List<List<Object>> values = new ArrayList<>();
    private final Map<String, ValueGenerator<?>> generators = new HashMap<>();
    private final List<String> properties = new ArrayList<>();
    private final List<IndexedValue> indexes = new ArrayList<>();

    public EntityWrapper(@NotNull DataEntity entity, EntityData data) {
        this.entity = entity;
        this.entityName = entity.name();
        this.count = data.count();
        var propIndex = 0;
        var indexedProps = new HashMap<Integer, Set<Integer>>();

        BiFunction<Integer, Set<Integer>, Set<Integer>> insertIndex = (i, v) -> { if (v == null) {v = new HashSet<>();} v.add(i); return v;};

        for (var property : entity.properties()) {
            // List Reference
            if (property.reference() != null) {
                dependencies.add(property.reference().entity());
                references.put(property.name(), property.reference());
            }
            // extract indexed properties
            if (property.idIndex() > -1) {
                indexedProps.compute(propIndex, insertIndex);
            }

            // Create generators
            properties.add(property.name());
            generators.put(property.name(), matchValueGenerator(property));
            propIndex++;
        }

        // Create indexes
        if (!indexedProps.isEmpty()) {
            for (var ii : indexedProps.values()) {
                indexes.add(new IndexedValue(
                        ii.stream().mapToInt(Integer::intValue).toArray()
                ));
            }
        }
    }

    protected ValueGenerator<?> matchValueGenerator(EntityProperty property) {
        return TypedValueGenerator.matchDefaultGeneratorByType(property);
    }

    public int getStatus() {
        return status.get();
    }

    public boolean updateStatus(int targetStatus) {
        return targetStatus - 1 >= 0 && status.compareAndSet(targetStatus - 1, targetStatus);
    }

    public boolean hasDependencies() {
        return !dependencies.isEmpty();
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
                .findFirst().map(p -> new TypedValue(p.type())).orElseThrow(() -> new IllegalArgumentException("Property " + propName + " isn't defined in " + entityName));
    }

    public TypedValue getReference(String name) {
        if (referenced.containsKey(name)) {
            return referenced.get(name);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof EntityWrapper) && StringUtils.equals(entityName, ((EntityWrapper) obj).getEntityName());
    }
}
