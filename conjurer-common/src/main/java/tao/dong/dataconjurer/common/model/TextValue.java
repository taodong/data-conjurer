package tao.dong.dataconjurer.common.model;

import lombok.Getter;

@Getter
public class TextValue extends StringValueSupplier<String> {

    public TextValue(String value) {
        super(value);
    }

    @Override
    public String get() {
        return value;
    }
}
