package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class TextValue extends StringValueSupplier<String> {

    public TextValue(String value) {
        super(value);
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TextValue tv) && StringUtils.equals(tv.get(), this.value);
    }
}
