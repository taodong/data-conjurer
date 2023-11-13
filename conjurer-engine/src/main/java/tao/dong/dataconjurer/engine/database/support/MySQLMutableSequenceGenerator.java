package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.support.MutableSequenceGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.INTERVAL;

public class MySQLMutableSequenceGenerator extends MutableSequenceGenerator {

    public MySQLMutableSequenceGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected ValueGenerator<Long> createGenerator(Set<Constraint<?>> constraints) {
        long current = 1;
        long leap = 1;
        for (var constraint : constraints) {
            if (INTERVAL == constraint.getType()) {
                var interval = (Interval)constraint;
                current = Math.max(interval.getBase(), 1L);
                leap = Math.max(interval.getLeap(), 1L);
                break;
            }
        }
        return new MySQLSequenceGenerator(current, leap);
    }

    @Override
    protected ValueGenerator<Long> getDefaultGenerator() {
        return new MySQLSequenceGenerator(1L, 1L);
    }
}
