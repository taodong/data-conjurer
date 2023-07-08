package tao.dong.dataconjurer.common.model;

import jakarta.annotation.PostConstruct;

/**
 * Ratio range is between (0, 1]
 */
public class RatioRange extends ValueRange<Double> {

    public RatioRange(Double min, Double max) {
        super(min, max, false, true);
    }

    @PostConstruct
    @Override
    protected void validate() {
        if (min < 0 || max > 1) {
            throw new IllegalArgumentException("Invalid ratio range. min " + min + " max: " + max);
        }
        super.validate();
    }
}
