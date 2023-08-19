package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.support.IndexValueGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private final int initSize;

    private final Map<String, Set<TextValue>> references = new HashMap<>();

    public EntityWrapper(@NotNull DataEntity entity, EntityData data) {
        this.entity = entity;
        this.count = data.count();
        this.initSize = this.count > 1000 ? 1000 : (int)this.count;
        var deps = entity.getProperties()
                .stream()
                .map(EntityProperty::getReference)
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

    public void addReference(String... properties) {
        if (properties != null) {
            for (var prop : properties) {
                references.computeIfAbsent(prop, ignore -> new HashSet<>(initSize));
            }
        }
    }


}
