package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.model.TextValue;

public class MySQLNumberValue extends TextValue {
    public MySQLNumberValue(Object value) {
        super(String.valueOf(value));
    }
}
