package tao.dong.dataconjurer.common.support;

import lombok.extern.slf4j.Slf4j;
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
        if (NULL_KEY.getMatcher().apply(val)) {
            return null;
        }

        try {
            return switch (type) {
                case SEQUENCE -> Long.valueOf(val);
                case NUMBER -> new BigDecimal(val);
                case DATE -> DataHelper.convertFormattedStringToMillisecond(val, DATE_FORMAT.getFormat());
                case DATETIME -> DataHelper.convertFormattedStringToMillisecond(val, DATETIME_FORMAT.getFormat());
                case TEXT -> val;
            };
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
