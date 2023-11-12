package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface DataHelper {
    static <T, K> void appendToSetValueInMap(Map<K, Set<T>> map, K key, T val) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(val);
    }

    static String formatMilliseconds(long milliseconds, @NotEmpty String pattern) {
        var instant = Instant.ofEpochMilli(milliseconds);
        var formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(instant.atZone(ZoneId.of("UTC")));
    }
}
