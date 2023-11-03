package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityWrapperTest {

    private static final EntityTestHelper testHelper = new EntityTestHelper();

    @Test
    void testUpdateStatus() {
        var test = new EntityWrapper(testHelper.createSimpleEntity(), testHelper.createSimpleData(null, null));
        assertEquals(0, test.getStatus());
        test.updateStatus(2);
        assertEquals(0, test.getStatus());
        test.updateStatus(-1);
        assertEquals(0, test.getStatus());
        test.updateStatus(1);
        assertEquals(1, test.getStatus());
        test.updateStatus(0);
        assertEquals(1, test.getStatus());
        test.updateStatus(2);
        assertEquals(2, test.getStatus());
    }

    private static Stream<Arguments> testFailProcess() {
        return Stream.of(
                Arguments.of(new int[]{}, -1),
                Arguments.of(new int[]{1}, -1),
                Arguments.of(new int[]{1, 2}, 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testFailProcess(int[] sequence, int expected) {
        var test = new EntityWrapper(testHelper.createSimpleEntity(), testHelper.createSimpleData(null, null));
        for (var status : sequence) {
            test.updateStatus(status);
        }
        test.failProcess("negative test");
        assertEquals(expected, test.getStatus());
    }

    private static Stream<Arguments> testEquals() {
        return Stream.of(
                Arguments.of(
                        new EntityWrapper(testHelper.createSimpleEntity(), testHelper.createSimpleData(null, null)),
                        new EntityWrapper(testHelper.createSimpleEntity(), testHelper.createSimpleData(null, null)),
                        true
                ),
                Arguments.of(
                        new EntityWrapper(testHelper.createSimpleEntity(), testHelper.createSimpleData(null, null)),
                        new EntityWrapper(testHelper.createSimpleEntity(), testHelper.createSimpleDataWithId(null, null, 1)),
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void testEquals(EntityWrapper obj1, EntityWrapper obj2, boolean expected) {
        assertEquals(expected, obj1.equals(obj2));
    }

}
