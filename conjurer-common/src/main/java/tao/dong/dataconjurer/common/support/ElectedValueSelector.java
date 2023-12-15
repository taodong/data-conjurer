package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.ReferenceStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static tao.dong.dataconjurer.common.model.ReferenceStrategy.LOOP;
import static tao.dong.dataconjurer.common.model.ReferenceStrategy.RANDOM;

@Slf4j
public class ElectedValueSelector implements ValueGenerator<Object> {

    private final List<Object> values = new ArrayList<>();
    private final IndexValueGenerator indexValueGenerator;

    public ElectedValueSelector(@NotEmpty Collection<?> values) {
        this(values, RANDOM);
    }

    public ElectedValueSelector(Collection<?> values, ReferenceStrategy strategy) {
        this.values.addAll(values);
        this.indexValueGenerator = strategy == LOOP ? new LoopIndexGenerator(this.values.size()) : new RandomIndexGenerator(this.values.size());
    }

    @Override
    public Object generate() {
        var index = indexValueGenerator.generate();
        return values.get(index);
    }
}
