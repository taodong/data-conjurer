package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tao.dong.dataconjurer.common.validation.FormulaVariableCheck;

import java.math.BigDecimal;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CORRELATION;

@JsonTypeName("correlation")
@FormulaVariableCheck
public record NumberCorrelation(@NotEmpty Set<@NotBlank String> properties, @NotBlank String formula) implements Constraint<BigDecimal>{
    @Override
    public boolean isMet(BigDecimal val) {
        return true; // short circuit. No validation method is available
    }

    @Override
    public ConstraintType getType() {
        return CORRELATION;
    }
}
