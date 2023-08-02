package tao.dong.dataconjurer.common.support;

public class LoopIndexGenerator implements IndexValueGenerator{

    private int next;
    private final int size;

    public LoopIndexGenerator(int size) {
        this(size, 0);
    }

    public LoopIndexGenerator(int size, int next) {
        this.next = next;
        this.size = size;
        if (next < 0 || size < 1 || size <= next) {
            throw new IllegalArgumentException("Invalid loop index generator. size: " + size + " first index: " + next);
        }
    }

    @Override
    public Integer generate() {
        var index = this.next;
        this.next = (this.next + 1) % size;
        return index;
    }
}
