package tao.dong.dataconjurer.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
public abstract class PropertyValue<T> implements Supplier<String> {
    protected final T value;

    public String get() {
        return String.valueOf(value);
    }
}
