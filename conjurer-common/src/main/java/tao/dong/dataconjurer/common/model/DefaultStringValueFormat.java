package tao.dong.dataconjurer.common.model;

import lombok.Getter;

@Getter
public enum DefaultStringValueFormat {
    DATE_FORMAT("yyyy-MM-dd"),
    DATETIME_FORMAT("yyyy-MM-dd HH:mm:ss");

    private final String format;

    DefaultStringValueFormat(String format) {
        this.format = format;
    }
}
