package tao.dong.dataconjurer.common.evalex;


import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import tao.dong.dataconjurer.common.support.RandomLongGenerator;

import java.math.BigDecimal;

import static com.ezylang.evalex.parser.Token.TokenType.FUNCTION;

@FunctionParameter(name = "start")
@FunctionParameter(name = "end")
public class TimeBetweenFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) throws EvaluationException {
        var start = evaluationValues[0].getNumberValue().longValue() + 1;
        var end = evaluationValues[1].getNumberValue().longValue();
        if (start >= end) {
            throw new EvaluationException(
                    new Token(0, "" + (end - start), FUNCTION),
                    "Start time must be less than end time for function TIME_BETWEEN");
        }
        var rs = new BigDecimal(RandomLongGenerator.builder().minInclusive(start).maxExclusive(end).build().generate());
        return EvaluationValue.numberValue(rs);
    }
}
