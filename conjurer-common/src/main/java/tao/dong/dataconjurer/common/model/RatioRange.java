package tao.dong.dataconjurer.common.model;

import jakarta.annotation.Nonnull;

/**
 * Ratio range is between (0, 1]
 */
public class RatioRange extends ValueRange<Double> {

    public RatioRange(@Nonnull Double min, @Nonnull Double max) {
        super(min, max, false, true);
    }

    @Override
    protected void validate() {
        super.validate();
        if (min < 0 || max > 1) {
            throw new IllegalArgumentException("Invalid ratio range. min " + min + " max: " + max);
        }
    }
}
