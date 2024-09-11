package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionParameter(name = "template")
@FunctionParameter(name = "values", isVarArg = true)
public class StringTemplateFunction extends AbstractFunction {

    private static final String DOUBLE_DOLLAR = "$$";
    private static final String INTERNAL_REPLACE = "<???TEMP???>";
    private static final String DOLLAR = "$";

    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) {
        var template = evaluationValues[0].getStringValue();
        var modifiedTemplate = StringUtils.replace(template, DOUBLE_DOLLAR, INTERNAL_REPLACE);
        var values = evaluationValues[1].getArrayValue();
        if (values.size() % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of arguments for function STRING_TEMPLATE");
        }

        final var valueMap = createValueMap(values);
        var substitutor = new StringSubstitutor(key -> valueMap.getOrDefault(key, ""));
        substitutor.setEnableUndefinedVariableException(false);
        substitutor.setValueDelimiter("");
        var result = substitutor.replace(modifiedTemplate);
        var modifiedResult = StringUtils.replace(result, INTERNAL_REPLACE, DOLLAR);

        return EvaluationValue.stringValue(modifiedResult);
    }

    private Map<String, String> createValueMap(List<EvaluationValue> values) {
        Map<String, String> valueMap = new HashMap<>();
        for (int i = 0; i < values.size(); i += 2) {
            valueMap.put(values.get(i).getStringValue(), values.get(i + 1).getStringValue());
        }
        return valueMap;
    }
}
