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
        @JsonSubTypes.Type(value = ChainedValue.class, name = "chain"),
        @JsonSubTypes.Type(value = StringFormat.class, name = "format"),
        @JsonSubTypes.Type(value = StringAlternation.class, name = "alternation")
})
public interface Constraint<T> {
    // By default, isMet is not enforced
    default boolean isMet(T val) {
        return true;
    }
    ConstraintType getType();
}
