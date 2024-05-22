package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityWrapperIdTest {

    @Test
    void testGetIdString() {
        var id = new EntityWrapperId("t1", 1);
        assertEquals("t1_1", id.getIdString());
    }

    private static Stream<Arguments> testCompareTo() {
        return Stream.of(
                Arguments.of(new EntityWrapperId("t1", 0), new EntityWrapperId("t1", 0), 0),
                Arguments.of(new EntityWrapperId("t1", 0), new EntityWrapperId("t1", 1), -1),
                Arguments.of(new EntityWrapperId("t1", 1), new EntityWrapperId("t0", 2), 1),
                Arguments.of(new EntityWrapperId("t1", 1), null, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCompareTo")
    void testCompareTo(EntityWrapperId id1, EntityWrapperId id2, int expected) {
        assertEquals(expected, id1.compareTo(id2));
    }
}
