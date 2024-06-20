package tao.dong.dataconjurer.common.support;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import tao.dong.dataconjurer.common.evalex.EvalExOperation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.CALCULATION;

@Getter
public class NumberCalculator extends EvalExOperation implements ValueGenerator<BigDecimal> {

    private final Set<String> parameters = new HashSet<>();
    private final Expression formula;

    public NumberCalculator(@NotBlank String formulaStr, @NotEmpty Collection<String> parameters) {
        this.formula = new Expression(formulaStr, EXPRESSION_CONFIGURATION);
        this.parameters.addAll(parameters);
    }

    public BigDecimal calculate(@NotEmpty Map<String, Object> inputs) {
        try {
            var filtered = inputs.entrySet().stream()
                    .filter(entry -> parameters.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (filtered.size() != parameters.size()) {
                throw new DataGenerateException(CALCULATION, "Missing variable values for " + formula.getExpressionString());
            }
            var result = formula.withValues(inputs).evaluate();
            return result.getNumberValue();
        } catch (EvaluationException | ParseException e) {
            throw new DataGenerateException(CALCULATION, "Failed to calculate value for " + formula.getExpressionString(), e);
        }
    }

    @Override
    public BigDecimal generate() {
        throw new UnsupportedOperationException("Number calculator doesn't support value generation without variables");
    }
}
