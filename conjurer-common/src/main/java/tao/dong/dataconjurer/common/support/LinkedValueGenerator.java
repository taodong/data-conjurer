package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;

import java.util.Random;

public abstract class LinkedValueGenerator<T> implements ValueGenerator<T> {
    private final Random random = new Random();
    protected boolean head = true;
    protected final int direction;

    protected final double seed;
    protected final int style;
    protected T current;

    public LinkedValueGenerator(int direction, double seed, int style, @NotNull T first) {
        this.direction = Integer.compare(direction, 0);
        this.seed = seed;
        this.style = style;
        this.current = first;
    }

    @Override
    public T generate() {
        if (head) {
            head = false;
        } else {
            current = getNextValue();
        }
        return current;
    }

    protected abstract T getNextValue();

    protected double generateLeap() {
        return seed * generateChangeRatio() * (direction == 0 ? (random.nextDouble() > 0.5 ? -1 : 1) : direction);
    }

    protected double generateChangeRatio() {
        return switch (style) {
            case 1 ->  random.nextDouble();
            case 2 -> (random.nextGaussian() + 5) / 5;
            default -> 1.0;
        };
    }
}
