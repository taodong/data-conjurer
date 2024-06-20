package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@FunctionParameter(name = "secret")
@FunctionParameter(name = "saltLength")
@FunctionParameter(name = "iterations")
@FunctionParameter(name = "rawPassword")
public class Pbkdf2EncodeFunction extends AbstractFunction {

    @Override
    public EvaluationValue evaluate(Expression expression, Token token, EvaluationValue... evaluationValues) throws EvaluationException {
        var secret = evaluationValues[0].getStringValue();
        var saltLength = evaluationValues[1].getNumberValue().intValue();
        var iterations = evaluationValues[2].getNumberValue().intValue();
        var rawPassword = evaluationValues[3].getStringValue();

        var encoder = new Pbkdf2PasswordEncoder(secret, saltLength, iterations, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1);
        var encoded = encoder.encode(rawPassword);
        return EvaluationValue.stringValue(encoded);
    }
}
