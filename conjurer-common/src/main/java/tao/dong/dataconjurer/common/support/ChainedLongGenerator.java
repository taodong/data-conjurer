package tao.dong.dataconjurer.common.support;

public class ChainedLongGenerator extends ChainedValueGenerator<Long> {

    public ChainedLongGenerator(int direction, double seed, int style, Long head) {
        super(direction, seed, style, head);
    }

    @Override
    protected Long getNextValue() {
        return current + (long)generateLeap();
    }
}
