package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import tao.dong.dataconjurer.common.support.RandomLongGenerator;

import java.math.BigDecimal;
import java.util.Calendar;

@FunctionParameter(name = "anchor")
public class TimeAfterFunction extends AbstractFunction {
    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) {
        var anchor = evaluationValues[0];
        var min = anchor.getNumberValue().longValue() + 1;
        var cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 10);
        var rs = new BigDecimal(RandomLongGenerator.builder().minInclusive(min).maxExclusive(cal.getTimeInMillis()).build().generate());
        return EvaluationValue.numberValue(rs);
    }
}
