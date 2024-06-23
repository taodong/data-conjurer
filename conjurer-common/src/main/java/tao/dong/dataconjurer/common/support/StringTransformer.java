package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tao.dong.dataconjurer.common.evalex.EvalExOperation;

import java.util.Collection;
import java.util.Map;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.CALCULATION;

public class StringTransformer extends EvalExOperation<String> implements ValueGenerator<String> {


    public StringTransformer(@NotBlank String formulaStr, @NotEmpty Collection<String> parameters) {
        super(formulaStr, parameters);
    }

    @Override
    public String calculate(Map<String, Object> values) {
        try {
            var filtered = extractParameters(values);
            if (filtered.size() != parameters.size()) {
                throw new DataGenerateException(CALCULATION, "Missing variable values for " + formula.getExpressionString());
            }
            var result = formula.withValues(filtered).evaluate();
            return result.getStringValue();
        } catch (Exception e) {
            throw new DataGenerateException(DataGenerationErrorType.CALCULATION, "Failed to calculate value for " + formula.getExpressionString(), e);
        }
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.STRING;
    }

    @Override
    public String generate() {
        throw new UnsupportedOperationException("String transformer doesn't support value generation without variables");
    }
}
