package tao.dong.dataconjurer.common.support;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.UnfixedSize;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.LENGTH;
import static tao.dong.dataconjurer.common.model.ConstraintType.SIZE;

public class TextGeneratorDecorator implements ValueGenerator<String> {

    private final ValueGenerator<String> generator;
    public TextGeneratorDecorator(Set<Constraint<?>> constraints) {

        this.generator = matchGenerator(constraints);

    }

    private ValueGenerator<String> matchGenerator(Set<Constraint<?>> constraints) {
        if (CollectionUtils.isNotEmpty(constraints)) {
            for (var constraint : constraints) {
                if (StringUtils.equals(LENGTH.name(), constraint.getType())) {
                    var length = Math.toIntExact(((Length)constraint).getMax());
                    return new FixLengthStringGenerator(length);
                } else if (StringUtils.equals(SIZE.name(), constraint.getType())) {
                    var min = Math.toIntExact(((UnfixedSize)constraint).getMin());
                    var max = Math.toIntExact(((UnfixedSize)constraint).getMax());
                    return new RangeLengthStringGenerator(min, max);
                }
            }
        }

        return new RangeLengthStringGenerator(1, 100);
    }



    @Override
    public String generate() {
        return generator.generate();
    }
}
