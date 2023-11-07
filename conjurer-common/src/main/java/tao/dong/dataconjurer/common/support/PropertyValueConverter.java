package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public abstract class PropertyValueConverter<T> {

    private final BiFunction<Object, PropertyType, T> convertFun;

    public PropertyValueConverter(BiFunction<Object, PropertyType, T> convertFun) {
        this.convertFun = convertFun;
    }

    public final T convert(final Object val, final PropertyType type) {
        return convertFun.apply(val, type);
    }

    public final List<T> covertEntityProperties(@NotNull final List<Object> values, @NotNull final List<PropertyType> types) {
        if (values.size() != types.size()) {
            throw new IllegalArgumentException("Value and type number don't match. value: " + values.size() + " type: " + types.size());
        }

        return IntStream.range(0, values.size())
                .mapToObj(i -> convert(values.get(i), types.get(i)))
                .toList();
    }

}
