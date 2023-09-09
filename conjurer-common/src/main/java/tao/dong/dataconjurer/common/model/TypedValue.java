package tao.dong.dataconjurer.common.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TypedValue {
    private final PropertyType type;
    private final Set<Object> values = new HashSet<>();

    public void addValue(Object value) {
        if (value != null && type.getTargetClass().isInstance(value)) {
            values.add(value);
        } else {
            throw new IllegalArgumentException("value isn't compatible with " + type.getName());
        }
    }
}
