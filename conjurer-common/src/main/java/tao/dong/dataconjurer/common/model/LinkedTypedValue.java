package tao.dong.dataconjurer.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class LinkedTypedValue extends TypedValue {
    private final Map<String, Set<Object>> values = new ConcurrentHashMap<>();
    private final String linked;
    @Getter(AccessLevel.NONE)
    private final Map<String, List<Object>> orderedValues = new ConcurrentHashMap<>();
    private final List<String> orderedKeys = new ArrayList<>();
    private final List<Object> allValues = new ArrayList<>();

    public LinkedTypedValue(PropertyType type, String linked) {
        super(type);
        this.linked = linked;
    }

    /**
     * Add linked value, for example if the reference is defined as Reference("entity1", "property1", "property2"),
     * when add a linked value, the input should be (String.valueOf(entity1.property2), entity1.property1))
     * @param key - the string value of the property used to link the value
     * @param value - the value of the actual property
     */
    public void addLinkedValue(String key, Object value) {
        if (type.getTargetClass().isInstance(value)) {
            DataHelper.appendToSetValueInMap(values, key, value);
        } else {
            throw new IllegalArgumentException("value isn't compatible with " + type.getName());
        }
    }

    public synchronized List<Object> getKeyedValues(String key) {
        return orderedValues.computeIfAbsent(key, k -> new ArrayList<>(values.computeIfAbsent(k, k1 -> Collections.emptySet())));
    }

    public List<String> getOrderedKeys() {
        if (orderedKeys.isEmpty()) {
            orderedKeys.addAll(values.keySet());
        }
        return orderedKeys;
    }

    @Override
    public List<Object> getOrderedValues() {
        return allValues.isEmpty() ? values.values().stream().flatMap(Collection::stream).toList() : allValues;
    }

    @Override
    public DataType getDataType() {
        return DataType.LINKED;
    }

    @Override
    public void clearOrderedValues() {
        orderedValues.clear();
        allValues.clear();
        orderedKeys.clear();
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
