package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Interval;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.INTERVAL;

@Getter
public class SequenceGeneratorDecorator implements ValueGenerator<Long> {

    final protected SequenceGenerator generator;

    public SequenceGeneratorDecorator(Set<Constraint> constraints) {
        this.generator = createSequenceGenerator(calculateCurrentAndLeap(constraints));
    }

    protected SequenceGenerator createSequenceGenerator(SequenceSpec spec) {
        return new SequenceGenerator(spec.getCurrent(), spec.getLeap());
    }

    protected SequenceSpec calculateCurrentAndLeap(Set<Constraint> constraints) {
        for (var constraint : constraints) {
            if (StringUtils.equals(INTERVAL.name(), constraint.getType())) {
                var interval = (Interval)constraint;
                return new SequenceSpec(interval.getBase(), interval.getLeap());
            }
        }
        return getDefaultSpec();
    }

    protected SequenceSpec getDefaultSpec() {
        return new SequenceSpec(0L, 1L);
    }

    @Data
    @AllArgsConstructor
    protected static class SequenceSpec {
        long current;
        long leap;
    }

    @Override
    public Long generate() {
        return generator.generate();
    }
}
