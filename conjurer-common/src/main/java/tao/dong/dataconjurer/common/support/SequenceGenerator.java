package tao.dong.dataconjurer.common.support;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SequenceGenerator implements ValueGenerator<Long> {

    @Setter
    protected long current = 1;
    protected long leap = 1;

    public SequenceGenerator(long current, long leap) {
        this.current = current;
        this.leap = leap;
        if (leap == 0) {
            throw new IllegalArgumentException("Sequence leap is 0");
        }
    }

    public long calculateGeneratedValue(long rounds) {
        if (rounds > 0) {
            return current + (rounds - 1) * leap;
        } else {
            throw new IllegalArgumentException("Can't calculate generated value for " + rounds + " rounds");
        }
    }

    @Override
    public Long generate() {
        var previous = current;
        current += leap;
        return previous;
    }
}
