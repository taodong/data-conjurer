package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotNull;

import java.security.SecureRandom;
import java.util.Random;

public abstract class ChainedValueGenerator<T> implements ValueGenerator<T> {
    private final Random random = new SecureRandom();
    protected final int direction;

    protected final double seed;
    protected final int style;
    protected T current;

    protected ChainedValueGenerator(int direction, double seed, int style, @NotNull T head) {
        this.direction = Integer.compare(direction, 0);
        this.seed = seed;
        this.style = style;
        this.current = head;
    }

    @Override
    public T generate() {
        var previous = current;
        current = getNextValue();
        return previous;
    }

    protected abstract T getNextValue();

    protected double generateLeap() {
        return seed * generateChangeRatio() * getValueChangeDirection();
    }

    private int getValueChangeDirection() {
        if (direction == 0) {
            return random.nextDouble() > 0.5 ? -1 : 1;
        } else {
            return direction;
        }
    }

    protected double generateChangeRatio() {
        return switch (style) {
            case 1 ->  random.nextDouble();
            case 2 -> (Math.min(10, random.nextGaussian()) + 5) / 5;
            default -> 1.0;
        };
    }
}
