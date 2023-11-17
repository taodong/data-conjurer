package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class ElectedValueSelector implements ValueGenerator<Object> {

    private final List<Object> values = new ArrayList<>();
    private final IndexValueGenerator indexValueGenerator;

    public ElectedValueSelector(@NotEmpty Set<?> values, @NotNull IndexValueGenerator indexValueGenerator) {
        this.values.addAll(values);
        this.indexValueGenerator = indexValueGenerator;
    }

    @Override
    public Object generate() {
        var index = indexValueGenerator.generate();
        if (index < 0 || index >= values.size()) {
            LOG.warn("Generated index value {} is out of values allowed range, fallback to first value", index);
            index = 0;
        }
        return values.get(index);
    }
}
