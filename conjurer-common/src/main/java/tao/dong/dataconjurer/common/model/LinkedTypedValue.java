package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Data;
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

@Data
public class LinkedTypedValue {
    private final Map<String, Set<Object>> values = new HashMap<>();
    private final PropertyType type;
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, List<Object>> orderedValues = new HashMap<>();

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

    public void clearOrderedValues() {
        orderedValues.clear();
    }

    public void join(LinkedTypedValue ltv) {
        if (type != ltv.getType()) {
            throw new IllegalArgumentException("Can't join values of different types: " + type.getName() + " vs " + ltv.getType().getName());
        }
        var updated = Stream.of(values, ltv.getValues())
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
