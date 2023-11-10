package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Length.class, name = "length"),
        @JsonSubTypes.Type(value = UnfixedSize.class, name = "size"),
        @JsonSubTypes.Type(value = Precision.class, name = "precision"),
        @JsonSubTypes.Type(value = NumberRange.class, name="range")
})
public interface Constraint<T> {
    boolean isMet(T val);
    String getType();
}
