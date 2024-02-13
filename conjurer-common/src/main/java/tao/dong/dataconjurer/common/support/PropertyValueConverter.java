package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.MISC;

public abstract class PropertyValueConverter<T> {

    private final BiFunction<Object, PropertyType, T> convertFun;


    public PropertyValueConverter(BiFunction<Object, PropertyType, T> convertFun) {
        this.convertFun = convertFun;
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
        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future = executor.submit(
                    () -> records.parallelStream()
                            .map(record -> convertEntityProperties(record, types))
                            .toList()
            );
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new DataGenerateException(MISC, "Failed to convert to MySQL string values", e);
        }
    }

}
