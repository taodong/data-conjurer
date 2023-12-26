package tao.dong.dataconjurer.common.support;

import java.math.BigDecimal;

public class ChainedBigDecimalGenerator extends ChainedValueGenerator<BigDecimal> {


    public ChainedBigDecimalGenerator(int direction, double seed, int style, BigDecimal head) {
        super(direction, seed, style, head);
    }

    @Override
    protected BigDecimal getNextValue() {
        return current.add(BigDecimal.valueOf(generateLeap()));
    }
}
