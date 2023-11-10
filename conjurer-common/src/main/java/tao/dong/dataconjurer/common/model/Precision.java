package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.annotation.Nonnull;

import static tao.dong.dataconjurer.common.model.ConstraintType.PRECISION;

@JsonTypeName("precision")
@JsonIgnoreProperties({"min", "includeMin", "includeMax"})
public class Precision extends ValueRange<Integer>{
    private static final int MAX_PRECESSION = 10;

    public Precision(@Nonnull @JsonProperty("max") Integer max) {
        super(0, max, true, true);
    }

    @Override
    protected void validate() {
        super.validate();
        if (max > MAX_PRECESSION ) {
            throw new IllegalArgumentException("Precession is over maximum digits allowed. Max: " + MAX_PRECESSION + " Requested: " + max);
        }
    }

    @Override
    public String getType() {
        return PRECISION.name();
    }
}
