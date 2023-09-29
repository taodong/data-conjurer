package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Length.class, name = "length")
})
public interface Constraint<T> {
    boolean isMet(T val);
    String getType();
}
