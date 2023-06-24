package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.model.PropertyValue;

public class MySQLTextValue extends PropertyValue {
    private final String value;

    public MySQLTextValue(String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return "'" + value + "'";
    }
}
