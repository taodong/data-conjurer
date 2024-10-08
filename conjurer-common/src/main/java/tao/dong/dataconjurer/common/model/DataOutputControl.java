package tao.dong.dataconjurer.common.model;

import java.util.Set;

public record DataOutputControl(String name, Set<EntityOutputControl> entities, String extra) {
    public DataOutputControl(String name, Set<EntityOutputControl> entities) {
        this(name, entities, null);
    }
}
