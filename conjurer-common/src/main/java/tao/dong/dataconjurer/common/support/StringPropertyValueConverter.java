package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
public abstract class StringPropertyValueConverter<Object> {

    private final BiFunction<String, PropertyType, Object> convertFun;


    protected StringPropertyValueConverter(BiFunction<String, PropertyType, Object> convertFun) {
        this.convertFun = convertFun;
    }

    public final Object convert(final String val, final PropertyType type) {
        return convertFun.apply(val, type);
    }

    public final List<Object> convertValues(@NotNull final Collection<String> values, final PropertyType type) {
        return values.stream().map(val -> convert(val, type)).filter(this::filterConvertError).toList();
    }

    protected abstract boolean filterConvertError(Object o);
}
