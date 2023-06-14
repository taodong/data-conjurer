package tao.dong.dataconjurer.common.model;

import java.util.Set;

public record DataSchema(String name, Dialect dialect, Set<DataEntity> entities) {
}
