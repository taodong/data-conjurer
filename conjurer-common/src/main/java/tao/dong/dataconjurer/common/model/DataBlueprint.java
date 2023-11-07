package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

@Data
public class DataBlueprint {
    private final Map<EntityWrapperId, EntityWrapper> entities = new HashMap<>();
    private final Map<String, Set<EntityWrapperId>> entityWrapperIds = new HashMap<>();

    public void init(@NotEmpty Map<EntityWrapperId, EntityWrapper> entities,
                     @NotEmpty Map<String, Set<EntityWrapperId>> entityWrapperIds) {
        this.entities.putAll(entities);
        this.entityWrapperIds.putAll(entityWrapperIds);
    }

    public List<EntityWrapper> sortEntityByDependencies() {
        var ordered = new ArrayList<EntityWrapper>();
        var checked = new HashSet<String>();
        for (var entityName : entityWrapperIds.keySet()) {
            checkDependent(ordered, checked, entityName);
        }
        return ordered;
    }

    private void checkDependent(List<EntityWrapper> ordered, Set<String> checked, String entityName) {
        var wrapperIds = entityWrapperIds.get(entityName);
        for (var id : wrapperIds) {
            var wrapper = entities.get(id);
            Optional<String> unchecked;
            while ((unchecked = getUncheckedDependent(wrapper, checked)).isPresent()) {
                checkDependent(ordered, checked, unchecked.get());
            }
            if (wrapper.getStatus() == 2) {
                ordered.add(wrapper);
            }
        }
        checked.add(entityName);
    }

    private Optional<String> getUncheckedDependent(EntityWrapper wrapper, Set<String> checked) {
        return wrapper.getDependencies().stream().filter(checked::contains).findFirst();
    }

}
