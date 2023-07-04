package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.model.TextValue;

public class MySQLTextValue extends TextValue {


    public MySQLTextValue(String value) {
        super(MySQLDelimiter.QUOTE + value + MySQLDelimiter.QUOTE);
    }
}
