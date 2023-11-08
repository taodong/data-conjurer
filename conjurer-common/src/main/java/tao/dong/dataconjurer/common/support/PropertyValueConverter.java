package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.MISC;

public abstract class PropertyValueConverter<T> {

    private final BiFunction<Object, PropertyType, T> convertFun;
    private final int threadNum;

    public PropertyValueConverter(BiFunction<Object, PropertyType, T> convertFun) {
        this(convertFun, 4);
    }

    public PropertyValueConverter(BiFunction<Object, PropertyType, T> convertFun, int threadNum) {
        this.convertFun = convertFun;
        this.threadNum = threadNum > 0 ? threadNum : 1;
    }

    public final T convert(final Object val, final PropertyType type) {
        return convertFun.apply(val, type);
    }

    public final List<T> convertEntityProperties(@NotNull final List<Object> values, @NotNull final List<PropertyType> types) {
        if (values.size() != types.size()) {
            throw new IllegalArgumentException("Value and type number don't match. value: " + values.size() + " type: " + types.size());
        }

        return IntStream.range(0, values.size())
                .mapToObj(i -> convert(values.get(i), types.get(i)))
                .toList();
    }

    public final List<List<T>> convertRecords(@NotNull final List<List<Object>> records, @NotNull final List<PropertyType> types) {
        final var pool = new ForkJoinPool(threadNum);
        try {
            return pool.submit(() -> records.stream().parallel()
                    .map(record -> convertEntityProperties(record, types))
                    .toList()).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new DataGenerateException(MISC, "Failed to convert to MySQL string values", e);
       } finally {
            pool.shutdown();
        }
    }

}
