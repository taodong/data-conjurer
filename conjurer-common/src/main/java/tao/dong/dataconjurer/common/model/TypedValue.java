package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TypedValue {
    private final PropertyType type;
    private final Set<Object> values = new HashSet<>();
    @Setter(AccessLevel.NONE)
    private final List<Object> orderedValues = new ArrayList<>();

    public void addValue(Object value) {
        if (type.getTargetClass().isInstance(value)) {
            values.add(value);
        } else {
            throw new IllegalArgumentException("value isn't compatible with " + type.getName());
        }
    }

    public List<Object> getOrderedValues() {
        if (orderedValues.isEmpty()) {
            orderedValues.addAll(values);
        }
        return orderedValues;
    }

    @SuppressWarnings("unused")
    public List<Object> getOrderedValues(boolean reload) {
        if (reload) {
            orderedValues.clear();
        }
        return getOrderedValues();
    }
}
