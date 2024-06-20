package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@FunctionParameter(name = "rawPassword")
public class BcryptEncodeFunction  extends AbstractFunction {

    static final Map<Integer, BCryptPasswordEncoder> BC_ENCODERS = new HashMap<>();

    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) {
        var rawPassword = evaluationValues[0].getStringValue();
        var encoded = encode(rawPassword, -1);
        return EvaluationValue.stringValue(encoded);
    }

    protected String encode(String rawPassword, int strength) {
        if (strength <= 3 || strength > 10) {
            strength = -1;
        }
        var encoder = BC_ENCODERS.computeIfAbsent(strength, BCryptPasswordEncoder::new);
        return encoder.encode(rawPassword);
    }
}
