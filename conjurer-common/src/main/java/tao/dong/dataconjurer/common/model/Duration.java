package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static tao.dong.dataconjurer.common.model.ConstraintType.DURATION;

@JsonTypeName("duration")
public class Duration implements Constraint<Long> {

    public static final long OLDEST_TIME = -30609791999000L; // 1000-01-06T00:00:00 Proleptic Gregorian
    private final ValueRange<Long> valueRange;

    @JsonCreator
    public Duration(@JsonProperty("start") SecondMark start, @JsonProperty("end") SecondMark end) {

        this.valueRange = new ValueRange<>(extractMilliseconds(start, true),
                extractMilliseconds(end, false), true, false) {
            @Override
            public ConstraintType getType() {
                return DURATION;
            }
        };
    }

    public Long getMin() {
        return valueRange.getMin();
    }

    public Long getMax() {
        return valueRange.getMax();
    }

    // Java
    private Long extractMilliseconds(SecondMark mark, boolean isMin) {
        if (mark == null) {
            return isMin ? OLDEST_TIME : System.currentTimeMillis();
        }
        var zdt = ZonedDateTime.of(
                mark.getYear(), mark.getMonth(), mark.getDay(),
                mark.getHour(), mark.getMinute(), mark.getSecond(), 0,
                ZoneOffset.UTC);
        return zdt.toInstant().toEpochMilli();
    }

    @Override
    public boolean isMet(Long val) {
        return valueRange.isMet(val);
    }

    @Override
    public ConstraintType getType() {
        return valueRange.getType();
    }
}
