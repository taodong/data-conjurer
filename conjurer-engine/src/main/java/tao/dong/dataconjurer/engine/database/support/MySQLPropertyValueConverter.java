package tao.dong.dataconjurer.engine.database.support;

import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.common.model.StringValueSupplier;
import tao.dong.dataconjurer.common.support.PropertyValueConverter;
import tao.dong.dataconjurer.engine.database.model.MySQLDefaultValue;
import tao.dong.dataconjurer.engine.database.model.MySQLNullValue;
import tao.dong.dataconjurer.engine.database.model.MySQLNumberValue;
import tao.dong.dataconjurer.engine.database.model.MySQLTextValue;

import java.util.function.BiFunction;

public class MySQLPropertyValueConverter extends PropertyValueConverter<StringValueSupplier<String>> {

    private static final BiFunction<Object, PropertyType, StringValueSupplier<String>> CONVERT_TO_STRING_SUPPLIER = (value, property) -> {
        switch (property) {
            case SEQUENCE:
                return new MySQLNumberValue(value);
            case NUMBER:
                return new MySQLTextValue(value.toString());
            case TEXT:
            default:
                String pv = (String)value;
                if (pv == null) {
                    return new MySQLNullValue();
                } else if (StringUtils.containsAnyIgnoreCase(pv, "default")) {
                    return new MySQLDefaultValue();
                } else {
                    return new MySQLTextValue(pv);
                }
        }
    };



    public MySQLPropertyValueConverter() {
        super(CONVERT_TO_STRING_SUPPLIER);
    }
}
