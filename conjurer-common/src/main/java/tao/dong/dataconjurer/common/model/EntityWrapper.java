package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
class EntityWrapper {
    private final DataEntity entity;
    private final Set<String> dependencies = new HashSet<>();
    /**
     * status - entity process status
     *  0 - unprocessed
     *  1 - in process
     *  2 - completed
     */
    private final AtomicInteger status = new AtomicInteger(0);
    private final long count;

    private final Map<String, Set<TextValue>> references = new HashMap<>();

    public EntityWrapper(@NotNull DataEntity entity, EntityData data) {
        this.entity = entity;
        this.count = data.count();
        var deps = entity.properties()
                .stream()
                .map(EntityProperty::reference)
                .filter(Objects::nonNull)
                .map(Reference::entity)
                .collect(Collectors.toSet());
        if (!deps.isEmpty()) {
            dependencies.addAll(deps);
        }
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

    public void createReference(String... properties) {
        if (properties != null) {
            for (var prop : properties) {
                references.computeIfAbsent(prop, ignore -> new ListOrderedSet<>());
            }
        }
    }

    public void saveReference(@NotNull Map<String, TextValue> refs) {
        for (var entry : refs.entrySet()) {
            if (references.containsKey(entry.getKey())) {
                references.get(entry.getKey()).add(entry.getValue());
            } else {
                LOG.debug("Save unknown reference value {}.{}", entity.name(), entry.getKey());
            }
        }
    }

    public int getReferenceSize(String name) {
        if (references.containsKey(name)) {
            return references.get(name).size();
        }
        return 0;
    }

    public TextValue getReferenceValue(String name, int index) {
        try {
            if (references.containsKey(name)) {
                return ((ListOrderedSet<TextValue>) references.get(name)).get(index);
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            LOG.debug("Index calculation error found", e);
            return null;
        }
    }
}
