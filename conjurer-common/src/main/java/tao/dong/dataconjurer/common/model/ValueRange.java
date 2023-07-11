package tao.dong.dataconjurer.common.model;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Value range, maximum value has to be greater than minimum value
 * @param <T> - Comparable
 */
@Getter
@Setter
public abstract class ValueRange<T extends Comparable> implements Constraint<T>{
    @Nonnull
    protected T min;
    @Nonnull
    protected T max;
    protected boolean includeMin;
    protected boolean includeMax;

    public ValueRange(T min, T max, boolean includeMin, boolean includeMax) {
        this.min = min;
        this.max = max;
        this.includeMin = includeMin;
        this.includeMax = includeMax;
        validate();
    }

    @Override
    public boolean isMet(T val) {
        return (includeMax ? max.compareTo(val) >= 0 : max.compareTo(val) > 0) &&
               (includeMin ? min.compareTo(val) <= 0 : min.compareTo(val) < 0);
    }

    protected void validate() {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Invalid value range. min: " + min + " max: " + max);
        }
    }
}
