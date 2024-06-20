package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.config.ExpressionConfiguration;

import java.util.Map;

public abstract class EvalExOperation {
    protected static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("BCRYPT", new BcryptEncodeFunction()),
                    Map.entry("BCRYPT_STRENGTH", new BcryptEncodeWithStrengthFunction()),
                    Map.entry("PBKDF2_SHA1", new Pbkdf2EncodeFunction()),
                    Map.entry("TIME_AFTER", new TimeAfterFunction()),
                    Map.entry("PAST_TIME_AFTER", new PastTimeAfterFunction())
            );
}
