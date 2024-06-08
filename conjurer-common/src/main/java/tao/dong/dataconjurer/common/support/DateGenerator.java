package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.Constraint;

import java.util.Set;

public class DateGenerator extends DatetimeGenerator{
    public DateGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected double calculateSeed(double seed) {
        return seed * 86400000;
    }
}
