package tao.dong.dataconjurer.common.support;

import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.ConvertError;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.function.BiFunction;

@Slf4j
public class DefaultStringPropertyValueConverter extends StringPropertyValueConverter<Object>{
    private static final BiFunction<String, PropertyType, Object> CONVERT_FUN = (val, type) -> {
        try {
            return PropertyRowSerializer.deserializeProperty(val, type);
        } catch (Exception e) {
            LOG.warn("Failed to convert value {} to type {}", val, type.getName());
            return new ConvertError(val, e.getMessage());
        }
    };

    public DefaultStringPropertyValueConverter() {
        super(CONVERT_FUN);
    }

    protected boolean filterConvertError(Object o) {
        if (o instanceof ConvertError(String value, String message)) {
            LOG.warn("Drop unsuccessful converted value {}: {}", value, message);
            return false;
        }
        return true;
    }
}
