package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class IndexedValue {
    static final String NULL_VAL = "[_NULL_]";
    private static final String DELIMITER = "<->";

    private static final Function<Object, String> valueToString = o -> { if (o == null) {
            return NULL_VAL;
        } else {
            return String.valueOf(o);
        }
    };

    @Getter(AccessLevel.PACKAGE)
    private final LinkedHashSet<String> values = new LinkedHashSet<>();
    private final int[] indexOrders;

    public boolean addValue(@NotNull final List<Object> properties) {

        try {
            var val = Arrays.stream(indexOrders)
                    .mapToObj(properties::get)
                    .map(valueToString)
                    .collect(Collectors.joining(DELIMITER));

            return values.add(val);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void removeLastValue() {
        var it = values.iterator();
        while(it.hasNext()) {
            it.next();
        }
        it.remove();
    }
}
