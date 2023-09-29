package tao.dong.dataconjurer.common.model;

import static tao.dong.dataconjurer.common.model.ConstraintType.SIZE;

public class UnfixedSize extends ValueRange<Long> {

    protected UnfixedSize(Long min, Long max) {
        super(min, max, true, true);
        validate();
    }

    @Override
    public String getType() {
        return SIZE.name();
    }

    @Override
    protected void validate() {
        if (min < 0L) {
            throw new IllegalArgumentException("Invalid size with min: " + min);
        } else if (min.compareTo(max) == 0) {
            throw new IllegalArgumentException("For fixed size " + min + " use Length constraint");
        }
    }
}
