package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.Min;
import tao.dong.dataconjurer.common.model.CompoundValue;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public interface LocaleValueCollector {
    default List<CompoundValue> collect(@Min(1) int count, Supplier<CompoundValue> valueSupplier) {
        return IntStream.range(0, count).mapToObj(i -> valueSupplier.get()).toList();
    }

}
