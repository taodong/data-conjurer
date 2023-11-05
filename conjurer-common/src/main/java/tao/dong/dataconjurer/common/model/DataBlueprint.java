package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
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

}
