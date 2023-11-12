package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.support.DataHelper;

public class MySQLDateTimeValue extends MySQLTextValue{

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    public MySQLDateTimeValue(long value, String format) {
        super(DataHelper.formatMilliseconds(value, format));
    }

    public MySQLDateTimeValue(long value) {
        this(value, FORMAT);
    }
}
