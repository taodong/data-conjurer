package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public abstract class EvalExOperation<T> {

    protected static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("BCRYPT", new BcryptEncodeFunction()),
                    Map.entry("BCRYPT_STRENGTH", new BcryptEncodeWithStrengthFunction()),
                    Map.entry("PBKDF2_SHA1", new Pbkdf2EncodeFunction()),
                    Map.entry("TIME_AFTER", new TimeAfterFunction()),
                    Map.entry("PAST_TIME_AFTER", new PastTimeAfterFunction()),
                    Map.entry("TIME_BETWEEN", new TimeBetweenFunction())
            );

    protected final Set<String> parameters = new HashSet<>();
    protected final Expression formula;

    protected EvalExOperation(String formulaStr, Collection<String> parameters) {
        this.formula = new Expression(formulaStr, EXPRESSION_CONFIGURATION);
        this.parameters.addAll(parameters);
    }

    public abstract T calculate(Map<String, Object> values);

    protected Map<String, Object> extractParameters(Map<String, Object> values) {
        return values.entrySet().stream()
                .filter(entry -> parameters.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public abstract OperationType getOperationType();

    public enum OperationType {
        NUMBER,STRING
    }
}
