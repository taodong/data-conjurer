package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public interface LocaleStringValueCollector {
    default List<String> collect(@Min(1) int count, Supplier<String> valueSupplier) {
        return IntStream.range(0, count).mapToObj(i -> valueSupplier.get()).toList();
    }
}
