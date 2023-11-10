package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.math.BigDecimal;

import static tao.dong.dataconjurer.common.model.ConstraintType.NUMBER_RANGE;

@JsonTypeName("range")
public class NumberRange extends ValueRange<Long> {

    @JsonCreator
    public NumberRange(@JsonProperty("min") Long min, @JsonProperty("max") Long max) {
        super(min == null ? Long.MIN_VALUE : min, max == null ? Long.MAX_VALUE : max, true, false);
    }

    @Override
    public String getType() {
        return NUMBER_RANGE.name();
    }
}
