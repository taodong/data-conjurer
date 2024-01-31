package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.service.CharacterGroupService;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.service.DefaultDataProviderService;
import tao.dong.dataconjurer.common.support.DataGenerateException;
import tao.dong.dataconjurer.common.support.DefaultNameProvider;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class EntityWrapperTest {

    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();
    private final DataProviderService dataProviderService = mock(DataProviderService.class);

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
        var wrapper = new EntityWrapper(entity, plan, outputControl, dataProviderService, 0);
        assertEquals(1, wrapper.getHiddenIndex().size());
        assertEquals(1, wrapper.getAliases().size());
        assertEquals(2, wrapper.getIndexes().size());
    }

    @Test
    void testUpdateStatus() {
        var test = new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, dataProviderService, 0);
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
        var test = new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, dataProviderService, 0);
        for (var status : sequence) {
            test.updateStatus(status);
        }
        test.failProcess("negative test");
        assertEquals(expected, test.getStatus());
    }

    private static Stream<Arguments> testEquals() {
        return Stream.of(
                Arguments.of(
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null, 0),
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null, 0),
                        true
                ),
                Arguments.of(
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null, 0),
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleDataWithId(null, null, 1), null, null, 0),
                        false
                ),
                Arguments.of(
                        new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, null, 0),
                        "ABC", false

                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void testEquals(EntityWrapper obj1, Object obj2, boolean expected) {
        assertEquals(expected, obj1.equals(obj2));
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
        var test = new EntityWrapper(TEST_HELPER.createEntityT1(), TEST_HELPER.createSimpleData(null, null), null, dataProviderService, 0);
        test.createReferenced(new PropertyLink("t1p1", null), new PropertyLink("t1p2", null));
        var res = test.getReferencedByProperties(props);
        assertEquals(expected, res.size());
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
        var wrapper = new EntityWrapper(entity, data, null, dataProviderService, 0);
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
        var wrapper = new EntityWrapper(entity, data, null, dataProviderService, 0);
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
        var wrapper = new EntityWrapper(entity, data, null, dataProviderService, 0);
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
        var wrapper = new EntityWrapper(entity, data, null, dataProviderService, 0);
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
        var wrapper = new EntityWrapper(entity, data, null, dataProviderService, 0);
        assertNotEquals("abc", wrapper.getGenerators().get("t1p2").generate());
    }

    @Test
    void testGetPropertyOrder() {
        var wrapper = TEST_HELPER.getSimpleEntityWrapper();
        assertTrue(wrapper.getPropertyOrder("t1p1") >= 0);
    }

    @Test
    void testGetPropertyOrder_NonExisting() {
        var wrapper = TEST_HELPER.getSimpleEntityWrapper();
        assertEquals(-1, wrapper.getPropertyOrder("abc"));
    }

    private static Stream<Arguments> testCreateIndex() {
        return Stream.of(
                Arguments.of(Map.of(0, new EntityIndex(0, 0, 0)), IndexedValue.class),
                Arguments.of(Map.of(1, new EntityIndex(1, 1, 0), 2, new EntityIndex(1, 1, 0)), UnorderedIndexedValue.class),
                Arguments.of(Map.of(2, new EntityIndex(2, 2, 1), 3, new EntityIndex(2, 2, 2)), NonCircleIndexValue.class),
                Arguments.of(Map.of(3, new EntityIndex(3, 3, 1), 4, new EntityIndex(3, 3, 2)), NonCircleIndexValue.class),
                Arguments.of(Map.of(4, new EntityIndex(4, 4, 1), 5, new EntityIndex(5, 4, 1)), DistinctElementIndexedValue.class)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCreateIndex(Map<Integer, EntityIndex> indexDefs, Class<UniqueIndex<?>> expected) {
        var wrapper = TEST_HELPER.getSimpleEntityWrapper();
        var rs = wrapper.createIndex(indexDefs);
        assertTrue(expected.isInstance(rs));
    }

    private static Stream<Arguments> testCreateIndex_HandleErrors() {
        return Stream.of(
                Arguments.of(Map.of(1, new EntityIndex(1, 1, 0), 2, new EntityIndex(1, 0, 0))),
                Arguments.of(Map.of(1, new EntityIndex(1, 2, 0), 2, new EntityIndex(1, 2, 3)))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCreateIndex_HandleErrors(Map<Integer, EntityIndex> indexDefs) {
        var wrapper = TEST_HELPER.getSimpleEntityWrapper();
        assertThrows(DataGenerateException.class, () -> wrapper.createIndex(indexDefs));
    }

    @Test
    void testComplicatedWrapper() {
        var wrapper = new EntityWrapper(TEST_HELPER.createEntityT6(), TEST_HELPER.createDataT6(), TEST_HELPER.createOutputControlT6(),
                new DefaultDataProviderService(new CharacterGroupService(), new DefaultNameProvider()), 0);
        assertEquals(10, wrapper.getCount());
        assertEquals(4, wrapper.getReferences().size());
        assertEquals(3, wrapper.getDependencies().size());
        assertEquals(10, wrapper.getGenerators().size());
        assertEquals(3, wrapper.getIndexes().size());
        assertEquals(1, wrapper.getHiddenIndex().size());
        assertEquals(1, wrapper.getAliases().size());
        assertEquals(3, wrapper.getRefStrategy().size());
        assertEquals(1, wrapper.getEntries().size());
        assertEquals(1, wrapper.getCorrelated().size());
    }

}
