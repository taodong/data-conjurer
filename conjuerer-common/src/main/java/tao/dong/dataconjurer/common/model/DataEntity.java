package tao.dong.dataconjurer.common.model;

import java.util.Set;

public record DataEntity(String name, Set<EntityProperty> properties) {
}
