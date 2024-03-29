package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Value range, maximum value has to be greater than minimum value
 * @param <T> - Comparable
 */
@Getter
@Setter
public abstract class ValueRange<T extends Comparable<T>> implements Constraint<T>{
    protected T min;
    protected T max;
    protected boolean includeMin;
    protected boolean includeMax;

    protected ValueRange(T min, T max, boolean includeMin, boolean includeMax) {
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
        if (min == null || max == null) {
            throw new IllegalArgumentException("Null value found for required values");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Invalid value range. min: " + min + " max: " + max);
        }
    }
}
