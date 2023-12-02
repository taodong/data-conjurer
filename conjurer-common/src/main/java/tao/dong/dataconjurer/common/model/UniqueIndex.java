package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class UniqueIndex<T> {
    protected static final String NULL_VAL = "[_NULL_]";
    @Getter(AccessLevel.PACKAGE)
    protected final LinkedHashSet<T> values = new LinkedHashSet<>();
    protected static final Function<Object, String> VALUE_TO_STRING = o -> {
        if (o == null) {
            return NULL_VAL;
        } else {
            return String.valueOf(o);
        }
    };

    protected final int[] indexOrders;

    public abstract boolean addValue(@NotNull final List<Object> entry);

    public void removeLastValue() {
        var it = values.iterator();
        while(it.hasNext()) {
            it.next();
        }
        it.remove();
    }
}
