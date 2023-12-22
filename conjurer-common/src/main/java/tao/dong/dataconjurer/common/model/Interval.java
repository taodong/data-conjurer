package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import static tao.dong.dataconjurer.common.model.ConstraintType.INTERVAL;

@JsonTypeName("interval")
@Getter
public class Interval extends ValueRange<Long> {

    private final long base;

    @JsonCreator
    public Interval(@JsonProperty("leap") @NotNull Long leap, @JsonProperty("base") Long base) {
        super(0L, leap, false, true);
        if (base != null && base >= 0L) {
            this.base = base;
        } else {
            this.base = 1L;
        }
    }

    public Long getLeap() {
        return getMax();
    }

    @Override
    public boolean isMet(Long val) {
        return val != null && val.compareTo(base) >= 0 && (val - base) % max == 0;
    }

    @Override
    public ConstraintType getType() {
        return INTERVAL;
    }
}
