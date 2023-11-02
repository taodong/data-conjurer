package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityWrapperTest {

    private final EntityTestHelper testHelper = new EntityTestHelper();

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

}
