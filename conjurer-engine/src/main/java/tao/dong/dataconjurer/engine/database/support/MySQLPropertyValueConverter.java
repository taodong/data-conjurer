package tao.dong.dataconjurer.engine.database.support;

import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.common.model.StringValueSupplier;
import tao.dong.dataconjurer.common.model.TextValue;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.PropertyValueConverter;
import tao.dong.dataconjurer.engine.database.model.MySQLDateTimeValue;
import tao.dong.dataconjurer.engine.database.model.MySQLDateValue;
import tao.dong.dataconjurer.engine.database.model.MySQLDefaultValue;
import tao.dong.dataconjurer.engine.database.model.MySQLNullValue;
import tao.dong.dataconjurer.engine.database.model.MySQLNumberValue;
import tao.dong.dataconjurer.engine.database.model.MySQLTextValue;

import java.util.function.BiFunction;

public class MySQLPropertyValueConverter extends PropertyValueConverter<StringValueSupplier<String>> {

    private static final BiFunction<Object, PropertyType, StringValueSupplier<String>> CONVERT_TO_STRING_SUPPLIER = (value, property) -> {
        if (value == null) {
            return new MySQLNullValue();
        }

        return switch (property) {
            case SEQUENCE -> new MySQLNumberValue(value);
            case NUMBER -> new TextValue(value.toString());
            case DATE -> new MySQLDateValue(DataHelper.outputLongValue(value));
            case DATETIME -> new MySQLDateTimeValue(DataHelper.outputLongValue(value));
            case TIME -> new MySQLTextValue(DataHelper.formatTimeInSeconds(DataHelper.outputLongValue(value), "%02d:%02d:%02d"));
            case TEXT -> {
                String pv = String.valueOf(value);
                if (StringUtils.containsAnyIgnoreCase(pv, "<?default?>")) {
                    yield new MySQLDefaultValue();
                } else {
                    yield new MySQLTextValue(pv);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + property);
        };
    };

    public MySQLPropertyValueConverter() {
        super(CONVERT_TO_STRING_SUPPLIER);
    }
}
