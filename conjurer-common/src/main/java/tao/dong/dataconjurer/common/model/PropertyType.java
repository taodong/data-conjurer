package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Predicate;

@Getter
public enum PropertyType {
    TEXT("text", String.class),
    SEQUENCE("sequence", Long.class),
    NUMBER("number", BigDecimal.class),
    DATE("date", Long.class),
    DATETIME("datetime", Long.class)
    ;

    @JsonValue
    private final String name;
    private final Predicate<Dialect> support;
    private final Class targetClass;

    PropertyType(String name, Class targetClass) {
        this(name, targetClass, (Objects::nonNull));
    }

    PropertyType(String name, Class targetClass, Predicate<Dialect> support) {
        this.name = name;
        this.support = support;
        this.targetClass = targetClass;
    }
}
