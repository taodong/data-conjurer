package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import tao.dong.dataconjurer.common.model.TextValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ElectedTextValueGenerator implements ValueGenerator<TextValue>{

    private final IndexValueGenerator indexValueGenerator;
    private final List<TextValue> candidates = new ArrayList<>();

    public ElectedTextValueGenerator(IndexValueGenerator indexValueGenerator, @NotEmpty Set<TextValue> candidates) {
        this.indexValueGenerator = indexValueGenerator;
        this.candidates.addAll(candidates);
    }


    @Override
    public TextValue generate() {
        var index = indexValueGenerator.generate();
        return candidates.get(index);
    }
}
