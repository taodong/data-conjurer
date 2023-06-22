package tao.dong.dataconjurer.common.model;

import java.util.function.Supplier;

public abstract class PropertyValue implements Supplier<String> {
    private Object value;
    protected Class type;

    public String get() {
        return String.valueOf(value);
    }
}
