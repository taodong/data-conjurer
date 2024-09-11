package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;

@FunctionParameter(name = "value", isVarArg = true)
public class FullEmailAddressFunction extends AbstractFunction {
    private static final String START_ANGULAR_BRACKET = "<";
    private static final String END_ANGULAR_BRACKET = ">";

    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) throws EvaluationException {
        var params = evaluationValues[0].getArrayValue();
        var length = params.size();
        if (length == 0 || length > 2) {
            throw new EvaluationException(token, "Invalid number of arguments for function FULL_EMAIL_ADDRESS");
        }

        var address = params.get(0).getStringValue();
        String name = null;
        if (length == 1) {
            var atIndex = address.indexOf('@');
            if (atIndex > 0) {
                name = address.substring(0, atIndex);
            }
        } else {
            name = params.get(1).getStringValue();
        }

        if (name != null) {
            return EvaluationValue.stringValue(name + START_ANGULAR_BRACKET + address + END_ANGULAR_BRACKET);
        }

        return EvaluationValue.stringValue(address);
    }
}
