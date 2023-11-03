package tao.dong.dataconjurer.common.support;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface DataHelper {
    static <T, K> void appendToSetValueInMap(Map<K, Set<T>> map, K key, T val) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(val);
    }
}
