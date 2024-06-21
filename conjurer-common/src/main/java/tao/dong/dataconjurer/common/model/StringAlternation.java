package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tao.dong.dataconjurer.common.validation.FormulaVariableCheck;

import java.util.Set;

@JsonTypeName("alternation")
@FormulaVariableCheck
public record StringAlternation(@NotEmpty Set<@NotBlank String> properties, @NotBlank String formula)
        implements Constraint<String>, FormulaConstraint {
    @Override
    public ConstraintType getType() {
        return ConstraintType.ALTERNATION;
    }
}
