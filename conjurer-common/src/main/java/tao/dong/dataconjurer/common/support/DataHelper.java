package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        var formatter = new SimpleDateFormat(pattern);
        var date = formatter.parse(str);
        var cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    /**
     * Convert a Long or BigDecimal value to Long
     * @param val the value to convert
     * @return Long value
     * @throws NumberFormatException if the value is not a Long or BigDecimal
     */
    static Long outputLongValue(@NotNull Object val) {
        return switch (val) {
            case Long longVal -> longVal;
            case Number numberVal -> numberVal.longValue();
            default -> throw new NumberFormatException("Invalid number value: " + val);
        };
    }


    /**
     * Convert a time string to seconds
     * @param str the time string in format of "HH:mm:ss"
     * @return the time in seconds
     * @throws NumberFormatException if the string is not a valid time string
     */
    static Long convertTimeStringToSecond(String str) {
        var parts = str.split(":");
        if (parts.length != 3) {
            throw new NumberFormatException("Invalid time string: " + str);
        }
        var hour = Long.parseLong(parts[0]);
        var minute = Long.parseLong(parts[1]);
        var second = Long.parseLong(parts[2]);
        if (minute < 0 || minute >= 60 || second < 0 || second >= 60) {
            throw new NumberFormatException("Invalid time string: " + str);
        }
        return (hour >= 0 ? 1L : -1L) * (Math.abs(hour) * 3600 + Long.parseLong(parts[1]) * 60 + Long.parseLong(parts[2]));
    }

    /**
     * Format a time in seconds to a string in format of "HH:mm:ss"
     * @param seconds the time in seconds
     * @return the formatted time string
     */
    static String formatTimeInSeconds(long seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    static <T> List<T> removeIndexFromList(@NotNull List<T> list, @NotEmpty Set<Integer> indexes) {
        return IntStream.range(0, list.size())
                .filter(i -> !indexes.contains(i))
                .mapToObj(list::get)
                .toList();
    }

    static <T> Stream<T> streamNullableCollection(Collection<T> nullable) {
        return Optional.ofNullable(nullable).stream().flatMap(Collection::stream);
    }
}
