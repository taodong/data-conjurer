package tao.dong.dataconjurer.common.support;

public class LoopIndexGenerator implements IndexValueGenerator{

    private int next;
    private final int max;

    public LoopIndexGenerator(int max) {
        this(0, max);
    }

    public LoopIndexGenerator(int next, int max) {
        this.next = next;
        this.max = max;
        if (next < 0 || max < 1 || max < next) {
            throw new IllegalArgumentException("Invalid loop index generator. size: " + max + " first index: " + next);
        }
    }

    @Override
    public Integer generate() {
        var index = this.next;
        this.next = (this.next + 1) % max;
        return index;
    }
}
