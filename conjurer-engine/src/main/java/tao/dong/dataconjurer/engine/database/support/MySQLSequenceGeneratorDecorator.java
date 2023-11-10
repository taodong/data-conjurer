package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.support.SequenceGenerator;
import tao.dong.dataconjurer.common.support.SequenceGeneratorDecorator;

import java.util.Set;

public class MySQLSequenceGeneratorDecorator extends SequenceGeneratorDecorator {

    public MySQLSequenceGeneratorDecorator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected SequenceGenerator createSequenceGenerator(SequenceSpec spec) {
        return new MySQLSequenceGenerator(spec.getCurrent(), spec.getLeap());
    }

    @Override
    protected SequenceSpec getDefaultSpec() {
        return new SequenceSpec(1L, 1L);
    }
}
