package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Length.class, name = "length"),
        @JsonSubTypes.Type(value = UnfixedSize.class, name = "size"),
        @JsonSubTypes.Type(value = Precision.class, name = "precision"),
        @JsonSubTypes.Type(value = NumberRange.class, name="range"),
        @JsonSubTypes.Type(value = Duration.class, name="duration"),
        @JsonSubTypes.Type(value = CharacterGroup.class, name="char_group"),
        @JsonSubTypes.Type(value = ValueCategory.class, name="category"),
        @JsonSubTypes.Type(value = NumberCorrelation.class, name="correlation"),
        @JsonSubTypes.Type(value = Interval.class, name="interval"),
        @JsonSubTypes.Type(value = ChainedValue.class, name = "chain")
})
public interface Constraint<T> {
    boolean isMet(T val);
    ConstraintType getType();
}
