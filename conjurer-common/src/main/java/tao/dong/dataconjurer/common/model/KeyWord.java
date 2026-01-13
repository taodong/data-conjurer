package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

@Getter
public enum KeyWord {
    NULL_KEY((v) -> StringUtils.equalsIgnoreCase(StringUtils.trim(v), "<?null?>"), "<?null?>"),
    NOW_KEY((v) -> StringUtils.equalsIgnoreCase(StringUtils.trim(v), "<?now?>"), "<?now?>");

    private final Function<String, Boolean> matcher;
    private final String keyString;

    KeyWord(Function<String, Boolean> matcher, String keyString) {
        this.matcher = matcher;
        this.keyString = keyString;
    }
}
