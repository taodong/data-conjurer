package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@FunctionParameter(name= "str")
@FunctionParameter(name = "case")
public class StringCaseFunction extends AbstractFunction {

        @Override
        public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) {
            var str = evaluationValues[0].getStringValue();
            var caseType = evaluationValues[1].getStringValue();
            var result = switch (StringUtils.trim(StringUtils.lowerCase(caseType))) {
                case "upper" -> StringUtils.upperCase(str);
                case "lower" -> StringUtils.lowerCase(str);
                case "pascal" -> toPascalCase(str);
                case "title" -> StringUtils.capitalize(StringUtils.lowerCase(str));
                default -> str;
            };

            return EvaluationValue.stringValue(result);
        }

        private String toPascalCase(String str) {
            return Arrays.stream(str.split(" "))
                    .map(value -> StringUtils.capitalize(StringUtils.lowerCase(value)))
                    .collect(Collectors.joining(" "));
        }
}
