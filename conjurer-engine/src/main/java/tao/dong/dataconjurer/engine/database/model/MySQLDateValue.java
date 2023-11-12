package tao.dong.dataconjurer.engine.database.model;

public class MySQLDateValue extends MySQLDateTimeValue {
    private static final String FORMAT = "yyyy-MM-dd";

    public MySQLDateValue(long value) {
        super(value, FORMAT);
    }
}
