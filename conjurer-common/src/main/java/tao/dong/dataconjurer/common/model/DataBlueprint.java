package tao.dong.dataconjurer.common.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class DataBlueprint {
    private final Map<EntityWrapperId, EntityWrapper> entities = new HashMap<>();
    private final Map<String, Set<EntityWrapperId>> entityWrapperIds = new HashMap<>();
}
