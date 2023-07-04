package tao.dong.dataconjurer.common.support;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SequenceGenerator implements ValueGenerator<Long> {

    protected long current = 1;
    protected long leap = 1;

    public SequenceGenerator(long current, long leap) {
        this.current = current;
        this.leap = leap;
        if (leap == 0) {
            throw new IllegalArgumentException("Sequence leap is 0");
        }
    }

    @Override
    public Long generate() {
        var previous = current;
        current += leap;
        return previous;
    }
}
