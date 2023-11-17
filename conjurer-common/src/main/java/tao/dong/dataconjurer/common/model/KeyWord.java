package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

@Getter
public enum KeyWord {
    NULL_KEY((v) -> StringUtils.equalsIgnoreCase(StringUtils.trim(v), "<?null?>"));

    private final Function<String, Boolean> matcher;

    KeyWord(Function<String, Boolean> matcher) {
        this.matcher = matcher;
    }
}
