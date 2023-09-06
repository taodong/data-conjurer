package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.EntityProperty;

import java.util.Set;

public abstract class ValueGeneratorFactory<T> {

    protected abstract ValueGenerator createValueGenerator(EntityProperty property, Set<T> candidates);
}
