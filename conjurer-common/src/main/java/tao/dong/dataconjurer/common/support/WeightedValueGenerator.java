package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.common.model.RatioRange;
import tao.dong.dataconjurer.common.model.WeightedValue;

import java.util.List;

public class WeightedValueGenerator implements ValueGenerator<Object> {

    private final PropertyType type;
    private final List<WeightedValue> values;
    private final ValueGenerator<?> fallbackGenerator;
    private final RatioBucketsIndexGenerator ratioBucketsIndexGenerator;

    public WeightedValueGenerator(@NotNull PropertyType type, @NotEmpty List<WeightedValue> values, @NotNull ValueGenerator<?> fallbackGenerator) {
        this.type = type;
        this.values = values;
        this.fallbackGenerator = fallbackGenerator;
        var ratioBuckets = values.stream().map(WeightedValue::getWeighRange).toList();
        ratioBucketsIndexGenerator = new RatioBucketsIndexGenerator(ratioBuckets.toArray(RatioRange[]::new));
    }

    @Override
    public Object generate() {
        var index = ratioBucketsIndexGenerator.generate();
        return type.getTargetClass().cast(
                index != -1 ? values.get(index).getSelector().generate() : fallbackGenerator.generate()
        );
    }
}
