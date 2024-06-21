package tao.dong.dataconjurer.common.support;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tao.dong.dataconjurer.common.evalex.EvalExOperation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.CALCULATION;

public class NumberCalculator extends EvalExOperation<BigDecimal> implements ValueGenerator<BigDecimal> {

    public NumberCalculator(@NotBlank String formulaStr, @NotEmpty Collection<String> parameters) {
        super(formulaStr, parameters);
    }

    @Override
    public BigDecimal calculate(@NotEmpty Map<String, Object> inputs) {
        try {
            var filtered = extractParameters(inputs);
            if (filtered.size() != parameters.size()) {
                throw new DataGenerateException(CALCULATION, "Missing variable values for " + formula.getExpressionString());
            }
            var result = formula.withValues(filtered).evaluate();
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
