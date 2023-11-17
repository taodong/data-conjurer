package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.IntStream;

public interface DataHelper {
    static <T, K> void appendToSetValueInMap(Map<K, Set<T>> map, K key, T val) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(val);
    }

    static String formatMilliseconds(long milliseconds, @NotEmpty String pattern) {
        var instant = Instant.ofEpochMilli(milliseconds);
        var formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(instant.atZone(ZoneId.of("UTC")));
    }

    static Long convertFormattedStringToMillisecond(String str, @NotEmpty String pattern) throws ParseException {
        var formatter = new SimpleDateFormat(pattern);
        var date = formatter.parse(str);
        var cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    static <T> List<T> removeIndexFromList(@NotNull List<T> list, @NotEmpty Set<Integer> indexes) {
        return IntStream.range(0, list.size())
                .filter(i -> !indexes.contains(i))
                .mapToObj(list::get)
                .toList();
    }
}
