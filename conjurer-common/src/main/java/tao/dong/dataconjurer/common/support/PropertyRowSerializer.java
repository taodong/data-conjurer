package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SerializationException;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATETIME_FORMAT;
import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATE_FORMAT;
import static tao.dong.dataconjurer.common.model.KeyWord.NULL_KEY;

@Slf4j
public class PropertyRowSerializer {

    // private constructor to prevent instantiation
    private PropertyRowSerializer() {
        throw new IllegalStateException("Utility class");
    }
    
    private static final BiFunction<String, PropertyType, Object> DESERIALIZE_FUN = (val, type) -> {
        if (NULL_KEY.getMatcher().apply(val)) {
            return null;
        }

        try {
            return switch (type) {
                case SEQUENCE -> Long.valueOf(val);
                case NUMBER -> new BigDecimal(val);
                case DATE -> DataHelper.convertFormattedStringToMillisecond(val, DATE_FORMAT.getFormat());
                case DATETIME -> DataHelper.convertFormattedStringToMillisecond(val, DATETIME_FORMAT.getFormat());
                case BOOLEAN -> BooleanUtils.toBoolean(val);
                case TEXT -> val;
            };
        } catch (Exception e) {
            throw new SerializationException("Failed to convert value %s to type %s.".formatted(val, type.getName()));
        }
    };

    private static final BiFunction<Object, PropertyType, String> SERIALIZE_FUN = (val, type) -> {
        if (val == null) {
            return NULL_KEY.getKeyString();
        }

        try {
            return switch (type) {
                case SEQUENCE, NUMBER -> val.toString();
                case DATE -> DataHelper.formatMilliseconds((Long) val, DATE_FORMAT.getFormat());
                case DATETIME -> DataHelper.formatMilliseconds((Long) val, DATETIME_FORMAT.getFormat());
                case BOOLEAN -> String.valueOf(val);
                case TEXT -> (String) val;
            };
        } catch (Exception e) {
            throw new SerializationException("Failed to revert value %s to type %s.".formatted(val, type.getName()));
        }
    };

    public static List<String> serialize(@NotEmpty List<Object> row, @NotEmpty List<PropertyType> properties) {
        validateRow(row, properties);
        return IntStream.range(0, row.size())
                .mapToObj(i -> SERIALIZE_FUN.apply(row.get(i), properties.get(i)))
                .toList();
    }

    public static List<Object> deserialize(@NotEmpty List<String> row, @NotEmpty List<PropertyType> properties) {
        validateRow(row, properties);
        return IntStream.range(0, row.size())
                .mapToObj(i -> DESERIALIZE_FUN.apply(row.get(i), properties.get(i)))
                .toList();
    }

    private static <T, P> void validateRow(@NotEmpty List<T> row, @NotEmpty List<P> properties) {
        if (row.size() != properties.size()) {
            throw new SerializationException("Row size does not match property size");
        }
    }
}
