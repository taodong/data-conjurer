package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Getter
@Slf4j
public enum ReferenceStrategy {
    RANDOM, LOOP;

    public static ReferenceStrategy getByName(final String name) {
        if (name == null) {
            return RANDOM;
        }

        return switch (StringUtils.lowerCase(name)) {
            case "loop" -> LOOP;
            case "random" -> RANDOM;
            default -> {
                LOG.warn("Unsupported reference strategy {}. Fallback to random strategy", name);
                yield RANDOM;
            }
        };
    }

}
