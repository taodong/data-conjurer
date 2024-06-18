package tao.dong.dataconjurer.common.support;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "rawPassword")
@FunctionParameter(name = "strength")
public class BcryptEncodeWithStrengthFunction extends BcryptEncodeFunction{
    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) {
        var rawPassword = evaluationValues[0].getStringValue();
        var strength = evaluationValues[1].getNumberValue().intValue();
        var encoded = encode(rawPassword, strength);
        return EvaluationValue.stringValue(encoded);
    }
}
