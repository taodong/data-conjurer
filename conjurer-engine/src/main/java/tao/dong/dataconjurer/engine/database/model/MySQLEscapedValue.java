package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.model.TextValue;

import static tao.dong.dataconjurer.engine.database.model.MySQLDelimiter.STRING_QUOTE;

public class MySQLEscapedValue extends TextValue {
    public MySQLEscapedValue(Object value) {
        super(STRING_QUOTE.getDelimiter() + value.toString() + STRING_QUOTE.getDelimiter());
    }
}
