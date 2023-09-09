package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ElectedTextValueGenerator<T> implements ValueGenerator{

    private final IndexValueGenerator indexValueGenerator;
    private final List<T> candidates = new ArrayList<T>();

    public ElectedTextValueGenerator(IndexValueGenerator indexValueGenerator, @NotEmpty Set<T> candidates) {
        this.indexValueGenerator = indexValueGenerator;
        this.candidates.addAll(candidates);
    }


    @Override
    public T generate() {
        var index = indexValueGenerator.generate();
        return candidates.get(index);
    }
}
