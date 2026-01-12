package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

@Getter
public enum KeyWord {
    NULL_KEY((v) -> StringUtils.equalsIgnoreCase(StringUtils.trim(v), "<?null?>"), "<?null?>"),
    CURRENT_DATETIME_KEY((v) -> StringUtils.equalsIgnoreCase(StringUtils.trim(v), "<?current_datetime?>"), "<?current_datetime?>"),
    CURRENT_DATE_KEY((v) -> StringUtils.equalsIgnoreCase(StringUtils.trim(v), "<?current_date?>"), "<?current_date?>");

    private final Function<String, Boolean> matcher;
    private final String keyString;

    KeyWord(Function<String, Boolean> matcher, String keyString) {
        this.matcher = matcher;
        this.keyString = keyString;
    }
}
