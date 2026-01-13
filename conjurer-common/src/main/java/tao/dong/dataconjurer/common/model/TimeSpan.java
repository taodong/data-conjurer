package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.support.DataHelper;

import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATETIME_FORMAT;

@Slf4j
@JsonTypeName("span")
public class TimeSpan implements Constraint<Long>{

    private final ValueRange<Long> valueRange;

    @JsonCreator
    public TimeSpan(@JsonProperty("anchor") String anchor, @NotNull @JsonProperty("leap")TimeLeap leap) {
        var anchorValue = getAnchorValue(anchor);
        var leapValue = calculateLeapValue(leap);
        if (leapValue == 0L) {
            throw new IllegalArgumentException("TimeLeap cannot be zero for TimeSpan constraint.");
        }
        this.valueRange = leapValue > 0 ?
                new NumberRange(anchorValue, anchorValue + leapValue) : new NumberRange(anchorValue + leapValue, anchorValue);
    }

    private Long getAnchorValue(String anchor) {
        if (anchor == null || KeyWord.NOW_KEY.getMatcher().apply(anchor)) {
            return System.currentTimeMillis();
        }

        try {
            return DataHelper.convertFormattedStringToMillisecond(anchor, DATETIME_FORMAT.getFormat());
        } catch (Exception e) {
            LOG.warn("Failed to parse anchor time string {}, applying current time.", anchor);
            return System.currentTimeMillis();
        }
    }

    private Long calculateLeapValue(TimeLeap leap) {
        return leap.getYears() * 31536000000L +
               leap.getMonths() * 2592000000L +
               leap.getDays() * 86400000L +
               leap.getHours() * 3600000L +
               leap.getMinutes() * 60000L +
               leap.getSeconds() * 1000L;
    }

    public Long getMin() {
        return valueRange.getMin();
    }

    public Long getMax() {
        return valueRange.getMax();
    }

    @Override
    public boolean isMet(Long val) {
        return valueRange.isMet(val);
    }

    @Override
    public ConstraintType getType() {
        return ConstraintType.TIME_SPAN;
    }
}
