package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.support.SequenceGenerator;

public class MySQLSequenceGenerator extends SequenceGenerator {

    public MySQLSequenceGenerator(long current, long leap) {
        super(current, leap);
        if (current < 1 || leap <= 0) {
            throw new IllegalArgumentException("Invalid MySQL sequence setting. start: " + current + " leap: " + leap);
        }
    }
}
