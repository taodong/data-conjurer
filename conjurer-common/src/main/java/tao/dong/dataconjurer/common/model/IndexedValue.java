package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndexedValue extends UniqueIndex<String> {
    private static final String DELIMITER = "<->";

    public boolean addValue(@NotNull final List<Object> properties) {
        return addNewEntry(properties);
    }

    protected boolean addNewEntry(final List<Object> properties) {
        try {
            var val = Arrays.stream(indexOrders)
                    .mapToObj(properties::get)
                    .map(VALUE_TO_STRING)
                    .collect(Collectors.joining(DELIMITER));

            return values.add(val);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public IndexedValue(int[] indexOrders) {
        super(indexOrders);
    }
}
