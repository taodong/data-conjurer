package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SimpleTypedValue extends TypedValue {
    private final Set<Object> values = ConcurrentHashMap.newKeySet();
    @Setter(AccessLevel.NONE)
    private final List<Object> orderedValues = new ArrayList<>();

    public SimpleTypedValue(PropertyType type) {
        super(type);
    }

    public void addValue(Object value) {
        if (type.getTargetClass().isInstance(value)) {
            values.add(value);
        } else {
            throw new IllegalArgumentException("value isn't compatible with " + type.getName());
        }
    }

    @Override
    public List<Object> getOrderedValues() {
        if (orderedValues.isEmpty()) {
            orderedValues.addAll(values);
        }
        return orderedValues;
    }

    @Override
    public void join(TypedValue tv) {
        validateJoin(tv.getType(), tv.getDataType());
        values.addAll(((SimpleTypedValue)tv).getValues());
        clearOrderedValues();

    }
    @Override
    public DataType getDataType() {
        return DataType.SIMPLE;
    }

    @Override
    public void clearOrderedValues() {
        orderedValues.clear();
    }
}
