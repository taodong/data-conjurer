package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class SyntheticData {

    private final ListOrderedMap<String, EntityData> entities = new ListOrderedMap<>();
    private int processed = -1;



    public EntityData getUnprocessed() {
        if (processed != -1 && !isCompleted()) {
            return entities.getValue(processed);
        }
        return null;
    }

    public boolean isCompleted() {
        return processed == entities.size();
    }

    class EntityTracker {
        private final DataEntity entity;
        private final Set<String> dependencies = new HashSet<>();
        private boolean processed;
        private final Map<String, List<TextValue>> references = new HashMap<>();

        EntityTracker(@NotNull DataEntity entity) {
            this.entity = entity;
            var deps = entity.getProperties().stream().map(EntityProperty::getReference)
                    .filter(Objects::nonNull).map(Reference::entity).collect(Collectors.toSet());
            if (!deps.isEmpty()) {
                dependencies.addAll(deps);
            }
        }

        boolean hasDependencies() {
            return !dependencies.isEmpty();
        }

        void addReference(String... properties) {
            if (properties != null) {
                for (var prop : properties) {
                    references.computeIfAbsent(prop, ignore -> new ArrayList<>());
                }
            }
        }


    }
}
