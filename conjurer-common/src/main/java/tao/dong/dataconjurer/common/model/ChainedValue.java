package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static tao.dong.dataconjurer.common.model.ConstraintType.CHAIN;

@JsonTypeName("chain")
@Data
public class ChainedValue implements Constraint<Double> {

    private double seed;
    private int direction;
    private int style;

    @JsonCreator
    public ChainedValue(@JsonProperty("seed") @NotNull Double seed,
                        @JsonProperty("direction") @Min(-1)  @Max(1) int direction,
                        @JsonProperty("style") @Min(0) int style) {
        this.seed = seed;
        this.direction = direction;
        this.style = style;
    }

    @Override
    public ConstraintType getType() {
        return CHAIN;
    }
}
