package tao.dong.dataconjurer.engine.database.model;

import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.TextValue;

public class MySQLTextValue extends TextValue {

    public MySQLTextValue(String value) {
        super(MySQLDelimiter.QUOTE + StringUtils.replace(value, "'", "\\'") + MySQLDelimiter.QUOTE);
    }
}
