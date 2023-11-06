package tao.dong.dataconjurer.common.model;

import java.util.Comparator;
import java.util.Objects;

public record EntityWrapperId(String entityName, int dataId) implements Comparable<EntityWrapperId>{
    public String getIdString() {
        return entityName + "_" + dataId;
    }

    @Override
    public int compareTo(EntityWrapperId that) {
        if (that != null) {
            return Objects.compare(this, that, Comparator.comparing(EntityWrapperId::entityName).thenComparing(EntityWrapperId::dataId));
        } else {
            return -1;
        }
    }
}
