package tao.dong.dataconjurer.common.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.NumberRange;
import tao.dong.dataconjurer.common.model.Precision;

import java.math.BigDecimal;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.NUMBER_RANGE;
import static tao.dong.dataconjurer.common.model.ConstraintType.PRECISION;

public class RandomNumberGeneratorDecorator implements ValueGenerator<BigDecimal>{
    protected final RandomNumberGenerator generator;

    public RandomNumberGeneratorDecorator(Set<Constraint<?>> constraints) {
        var spec = getDefaultSpec();
        updateSpecFromConstraints(constraints, spec);
        this.generator = new RandomNumberGenerator(spec.getMax(), spec.getMin(), spec.getPrecision());
    }

    protected void updateSpecFromConstraints(Set<Constraint<?>> constraints, NumberSpec spec) {
        for (var constraint : constraints) {
            if (StringUtils.equals(NUMBER_RANGE.name(), constraint.getType())) {
                var range = (NumberRange) constraint;
                spec.setMax(range.getMax());
                spec.setMin(range.getMin());
            } else if (StringUtils.equals(PRECISION.name(), constraint.getType())) {
                spec.setPrecision(((Precision)constraint).getMax());
            }
        }
    }

    protected NumberSpec getDefaultSpec() {
        return new NumberSpec(Long.MAX_VALUE, Long.MIN_VALUE, 0);
    }

    @Data
    @AllArgsConstructor
    protected static class NumberSpec {
        long max;
        long min;
        int precision;
    }

    @Override
    public BigDecimal generate() {
        return generator.generate();
    }
}
