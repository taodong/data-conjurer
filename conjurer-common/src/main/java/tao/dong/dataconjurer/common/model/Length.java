package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.annotation.Nonnull;

@JsonTypeName("length")
@JsonIgnoreProperties({"min", "includeMin", "includeMax"})
public class Length extends ValueRange<Long> {

    public Length(@Nonnull @JsonProperty("max") Long max) {
        super(0L, max, false, true);
    }

    @Override
    protected void validate() {
        if (max < 1L) {
            throw new IllegalArgumentException("Invalid length " + max);
        }
        super.validate();
    }

    @Override
    public String toString() {
        return "Length{" + "max=" + max + '}';
    }
}
