package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class LinkedTypedValue extends TypedValue{
    private final Map<String, Set<Object>> values = new HashMap<>();
    private final String linked;
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, List<Object>> orderedValues = new HashMap<>();

    public LinkedTypedValue(PropertyType type, String linked) {
        super(type);
        this.linked = linked;
    }

    public void addLinkedValue(String key, Object value) {
        if (type.getTargetClass().isInstance(value)) {
            DataHelper.appendToSetValueInMap(values, key, value);
        } else {
            throw new IllegalArgumentException("value isn't compatible with " + type.getName());
        }
    }

    public List<Object> getOrderedValues(String key) {
        return orderedValues.computeIfAbsent(key, k -> new ArrayList<>(values.computeIfAbsent(k, k1 -> Collections.emptySet())));
    }

    @Override
    public DataType getDataType() {
        return DataType.LINKED;
    }

    @Override
    public void clearOrderedValues() {
        orderedValues.clear();
    }

    @Override
    public void join(TypedValue ltv) {
        validateJoin(ltv.getType(), ltv.getDataType());
        var updated = Stream.of(values, ((LinkedTypedValue)ltv).getValues())
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> {left.addAll(right); return left;}
                ));
        values.clear();
        clearOrderedValues();
        values.putAll(updated);
    }
}
