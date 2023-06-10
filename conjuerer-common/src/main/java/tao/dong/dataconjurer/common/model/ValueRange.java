package tao.dong.dataconjurer.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public abstract class ValueRange<T> {
    private T min;
    private T max;
    boolean includeMin;
    boolean includeMax;
}
