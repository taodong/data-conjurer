package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Interval;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.INTERVAL;

public class SequenceGeneratorDecorator implements ValueGenerator<Long> {

    private final SequenceGenerator generator;

    public SequenceGeneratorDecorator(Set<Constraint> constraints) {
        var current = 0L;
        var leap = 1L;
        for (var constraint : constraints) {
            if (StringUtils.equals(INTERVAL.name(), constraint.getType())) {
                var interval = (Interval)constraint;
                current = interval.getBase();
                leap = interval.getLeap();
                break;
            }
        }
        this.generator = new SequenceGenerator(current, leap);
    }

    @Override
    public Long generate() {
        return generator.generate();
    }
}
