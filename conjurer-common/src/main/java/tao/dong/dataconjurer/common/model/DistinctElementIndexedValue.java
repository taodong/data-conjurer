package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DistinctElementIndexedValue extends IndexedValue{
    private final int[] distinctElements;

    public DistinctElementIndexedValue(int[] indexOrders, int[] distinctElements) {
        super(indexOrders);
        this.distinctElements = distinctElements == null ? new int[]{} : distinctElements;
    }

    @Override
    public boolean addValue(@NotNull final List<Object> properties) {
        return checkDistinctValues(properties) && addNewEntry(properties);
    }

    private boolean checkDistinctValues (final List<Object> properties) {
        if (distinctElements.length > 0) {
            try {
                var els = Arrays.stream(distinctElements)
                        .mapToObj(properties::get)
                        .map(VALUE_TO_STRING)
                        .collect(Collectors.toSet());
                return els.size() == distinctElements.length;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }
}
