package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityOutputControlTest {

    private static Stream<Arguments> testEquals(){
        return Stream.of(
                Arguments.of(new EntityOutputControl("entity1", Set.of()), new EntityOutputControl("entity1", null), true),
                Arguments.of(new EntityOutputControl("entity1", Set.of()), new EntityOutputControl("entity2", Set.of()), false),
                Arguments.of(new EntityOutputControl("entity1", Set.of()), "string", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testEquals(EntityOutputControl control1, Object control2, boolean expected) {
        assertEquals(expected, control1.equals(control2));
    }
}
