package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.model.TextValue;

import java.util.Objects;

public class MySQLNumberValue extends TextValue {
    public MySQLNumberValue(Object value) {
        super(String.valueOf(value));
    }
}
