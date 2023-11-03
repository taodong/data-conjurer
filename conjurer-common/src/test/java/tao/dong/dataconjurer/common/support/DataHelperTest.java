package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataHelperTest {

    @Test
    void testAppendToSetValueInMap() {
        Map<String, Set<String>> test = new HashMap<>();
        DataHelper.appendToSetValueInMap(test, "k1", "v1");
        assertEquals(1, test.get("k1").size());
        DataHelper.appendToSetValueInMap(test, "k1", "v2");
        assertEquals(1, test.size());
        assertEquals(2, test.get("k1").size());
    }
}
