package tao.dong.dataconjurer.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class ValueRange<T extends Comparable> implements Constraint<T>{
    protected T min;
    protected T max;
    protected boolean includeMin;
    protected boolean includeMax;

    @Override
    public boolean isMet(T val) {
        return (includeMax ? max.compareTo(val) >= 0 : max.compareTo(val) > 0) &&
               (includeMin ? min.compareTo(val) <= 0 : min.compareTo(val) < 0);
    }
}
