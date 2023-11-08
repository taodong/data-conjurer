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

@Data
public class DataBlueprint {
    private final Map<EntityWrapperId, EntityWrapper> entities = new HashMap<>();
    private final Map<String, Set<EntityWrapperId>> entityWrapperIds = new HashMap<>();

    public void init(@NotEmpty Map<EntityWrapperId, EntityWrapper> entities,
                     @NotEmpty Map<String, Set<EntityWrapperId>> entityWrapperIds) {
        this.entities.putAll(entities);
        this.entityWrapperIds.putAll(entityWrapperIds);
    }

    public List<EntityDataOutput> outputGeneratedData() {
        var entityOrders = new HashMap<String, Integer>();
        var current = 0;
        var results = new ArrayList<EntityDataOutput>();

        var processed = sortEntityByDependencies();
        for (var wrapper : processed) {
            var entityName = wrapper.getEntityName();
            var index = entityOrders.get(entityName);
            EntityDataOutput result;
            if (index == null) {
                result = new EntityDataOutput(entityName, wrapper.getPropertyTypes(), wrapper.getProperties());
                entityOrders.put(entityName, current++);
                results.add(result);
            } else {
                result = results.get(index);
            }
            result.addValues(wrapper.getValues());

        }
        return results;
    }

    List<EntityWrapper> sortEntityByDependencies() {
        var ordered = new ArrayList<EntityWrapper>();
        var checked = new HashSet<String>();
        for (var entityName : entityWrapperIds.keySet()) {
            checkDependent(ordered, checked, entityName);
        }
        return ordered;
    }

    private void checkDependent(List<EntityWrapper> ordered, Set<String> checked, String entityName) {
        if (!checked.contains(entityName)) {
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
    }

    private Optional<String> getUncheckedDependent(EntityWrapper wrapper, Set<String> checked) {
        return wrapper.getDependencies().stream().filter(name -> !checked.contains(name)).findFirst();
    }

}
