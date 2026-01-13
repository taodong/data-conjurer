package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SerializationException;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATETIME_FORMAT;
import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATE_FORMAT;
import static tao.dong.dataconjurer.common.model.KeyWord.NOW_KEY;
import static tao.dong.dataconjurer.common.model.KeyWord.NULL_KEY;

@Slf4j
public class PropertyRowSerializer {

    // private constructor to prevent instantiation
    private PropertyRowSerializer() {
        throw new IllegalStateException("Utility class");
    }

    public static Object deserializeProperty(String value, PropertyType type) throws ParseException {
        if (NULL_KEY.getMatcher().apply(value)) {
            return null;
        } else if (NOW_KEY.getMatcher().apply(value)) {
            return System.currentTimeMillis();
        }

        return switch (type) {
            case SEQUENCE -> Long.valueOf(value);
            case NUMBER -> new BigDecimal(value);
            case DATE -> DataHelper.convertFormattedStringToMillisecond(value, DATE_FORMAT.getFormat());
            case DATETIME -> DataHelper.convertFormattedStringToMillisecond(value, DATETIME_FORMAT.getFormat());
            case BOOLEAN -> BooleanUtils.toBoolean(value);
            case TIME -> {
                try {
                    yield DataHelper.convertTimeStringToSecond(value);
                } catch (Exception e) {
                    LOG.error("Failed to convert time string {} to seconds, applying 0", value);
                    yield  0L;
                }
            }
            case TEXT -> value;
        };
    }

    private static final BiFunction<String, PropertyType, Object> DESERIALIZE_FUN = (val, type) -> {
        try {
            return deserializeProperty(val, type);
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
                case TIME -> DataHelper.formatTimeInSeconds((Long) val, "%02d:%02d:%02d");
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
