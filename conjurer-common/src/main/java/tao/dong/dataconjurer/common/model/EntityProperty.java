package tao.dong.dataconjurer.common.model;


import java.util.List;

public record EntityProperty(String name, PropertyType type, boolean required, boolean id, List<Constraint> constraints, Reference reference) {
}
