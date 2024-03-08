package tao.dong.dataconjurer.common.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import tao.dong.dataconjurer.common.model.ConvertError;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATETIME_FORMAT;
import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATE_FORMAT;
import static tao.dong.dataconjurer.common.model.KeyWord.NULL_KEY;

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
        if (o instanceof ConvertError ce) {
            LOG.warn("Drop unsuccessful converted value {}: {}", ce.value(), ce.message());
            return false;
        }
        return true;
    }
}
