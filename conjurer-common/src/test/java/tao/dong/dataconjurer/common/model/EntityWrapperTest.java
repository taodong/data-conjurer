package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

class EntityWrapperTest {

    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();
    private final V1DataProviderApi dataProviderApi = mock(V1DataProviderApi.class);

    @Test
    void testConstruct() {
        var entity = TEST_HELPER.createEntityT5();
        var plan = TEST_HELPER.createSimpleData("t5", 5L);
        var outputControl = new EntityOutputControl("t5",
                Set.of(
                        new PropertyOutputControl("t5p1", true, null),
                        new PropertyOutputControl("t5p2", false, "p2")
                )
        );
        var wrapper = new EntityWrapper(entity, plan, outputControl, dataProviderApi);
        assertEquals(1, wrapper.getHiddenIndex().size());
        assertEquals(1, wrapper.getAliases().size());
        assertEquals(2, wrapper.getIndexes().size());
    }

    @Test
    void testUpdateStatus() {
        var test = new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, dataProviderApi);
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
        var test = new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, dataProviderApi);
        for (var status : sequence) {
            test.updateStatus(status);
        }
        test.failProcess("negative test");
        assertEquals(expected, test.getStatus());
    }

    private static Stream<Arguments> testEquals() {
        return Stream.of(
                Arguments.of(
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null),
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null),
                        true
                ),
                Arguments.of(
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null),
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleDataWithId(null, null, 1), null, null),
                        false
                )
        );
    }

    private static Stream<Arguments> testCreateReferenced() {
        return Stream.of(
                Arguments.of(new String[]{"t1p1"}, 1),
                Arguments.of(new String[]{"t1p2"}, 1),
                Arguments.of(new String[]{"t1p1", "t1p2"}, 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCreateReferenced(String[] props, int expected) {
        var test = new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, dataProviderApi);
        test.createReferenced("t1p1", "t1p2");
        var res = test.getReferencedByProperties(props);
        assertEquals(expected, res.size());
    }

    @ParameterizedTest
    @MethodSource
    void testEquals(EntityWrapper obj1, EntityWrapper obj2, boolean expected) {
        assertEquals(expected, obj1.equals(obj2));
    }

    @Test
    void testProcessEntries() {
        var entity = TEST_HELPER.createEntityT1();
        var data = EntityTestHelper.entityDataBuilder()
                .entity("t1")
                .entries(
                        new EntityEntry(List.of("t1p2", "t1p3"), List.of(List.of("1", "test1"), List.of("1", "test2"), List.of("abc", "abc")))
                )
                .build();
        var wrapper = new EntityWrapper(entity, data, null, dataProviderApi);
        assertEquals(2, wrapper.getEntries().size());
        assertEquals(2, wrapper.getEntries().get("t1p2").size());
    }

    @Test
    void testHandleDefaultValue() {
        var entity = TEST_HELPER.createEntityT1();
        var data = EntityTestHelper.entityDataBuilder()
                .entity("t1")
                .properties(
                        Set.of(
                                new PropertyInputControl("t1p3", null, "abc", null, null)
                        )
                )
                .build();
        var wrapper = new EntityWrapper(entity, data, null, dataProviderApi);
        for (var i = 0; i < 10; i++) {
            assertEquals("abc", wrapper.getGenerators().get("t1p3").generate());
        }
    }

    @Test
    void testHandleDefaultValue_ParseFailure() {
        var entity = TEST_HELPER.createEntityT1();
        var data = EntityTestHelper.entityDataBuilder()
                .entity("t1")
                .properties(
                        Set.of(
                                new PropertyInputControl("t1p2", null, "abc", null, null)
                        )
                )
                .build();
        var wrapper = new EntityWrapper(entity, data, null, dataProviderApi);
        assertNotEquals("abc", wrapper.getGenerators().get("t1p2").generate());
    }

    @Test
    void testHandleWeightedValue() {
        var entity = TEST_HELPER.createEntityT1();
        var data = EntityTestHelper.entityDataBuilder()
                .entity("t1")
                .properties(
                        Set.of(
                                new PropertyInputControl("t1p3", List.of(
                                        new PropertyValueDistribution(Set.of("abc"), 1.0)
                                ), null, null, null)
                        )
                )
                .build();
        var wrapper = new EntityWrapper(entity, data, null, dataProviderApi);
        for (var i = 0; i < 10; i++) {
            assertEquals("abc", wrapper.getGenerators().get("t1p3").generate());
        }
    }

    @Test
    void testHandleWeightedValue_ParseFailure() {
        var entity = TEST_HELPER.createEntityT1();
        var data = EntityTestHelper.entityDataBuilder()
                .entity("t1")
                .properties(
                        Set.of(
                                new PropertyInputControl("t1p2", List.of(
                                        new PropertyValueDistribution(Set.of("abc"), 1.0)
                                ), null, null, null)
                        )
                )
                .build();
        var wrapper = new EntityWrapper(entity, data, null, dataProviderApi);
        assertNotEquals("abc", wrapper.getGenerators().get("t1p2").generate());
    }

}
