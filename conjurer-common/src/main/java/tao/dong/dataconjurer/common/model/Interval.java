package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotNull;

public class Interval extends ValueRange<Long> {

    private final long base;

    public Interval(@NotNull Long leap, Long base) {
        super(0L, leap, false, true);
        if (base != null && base >= 0L) {
            this.base = base;
        } else {
            this.base = 0L;
        }
    }

    @Override
    public boolean isMet(Long val) {
        return val != null && val.compareTo(base) >= 0 && (val - base) % max == 0;
    }
}
