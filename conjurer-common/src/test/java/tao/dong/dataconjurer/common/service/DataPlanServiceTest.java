package tao.dong.dataconjurer.common.service;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataOutputControl;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityOutputControl;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.PropertyOutputControl;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.UnfixedSize;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static tao.dong.dataconjurer.common.model.Dialect.MYSQL;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;
import static tao.dong.dataconjurer.common.support.EntityTestHelper.entityIndexBuilder;
import static tao.dong.dataconjurer.common.support.EntityTestHelper.entityPropertyBuilder;

class DataPlanServiceTest {

    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();
    private final DataProviderService dataProviderService = mock(DataProviderService.class);

    @Test
    void testCreateDataBlueprint() {
        var service = new DataPlanService(dataProviderService);
        var schema = TEST_HELPER.createSimpleTestSchema();
        var plans = TEST_HELPER.createSimpleTestPlans();
        var config = DataGenerateConfig.builder()
                .maxIndexCollision(3)
                .build();
        var output = new DataOutputControl("control", Set.of(
                new EntityOutputControl("t1", null, Set.of(
                        new PropertyOutputControl("t1p1", false, "id"),
                        new PropertyOutputControl("t2p4", true, null)
                ))
        ));
        var blueprint = service.createDataBlueprint(schema, config, output, plans);
        assertEquals(5, blueprint.getEntities().size());
        assertEquals(11L, blueprint.getEntities().get(new EntityWrapperId("t3", 1)).getGenerators().get("t3p0").generate());
        assertEquals(1, blueprint.getEntities().get(new EntityWrapperId("t1", 0)).getAliases().size());
    }

    @Test
    void testCreateDataBlueprint_IgnorePlans() {
        var service = new DataPlanService(dataProviderService);
        var schema = TEST_HELPER.createSimpleTestSchema();
        var plans = new DataPlan[]{
                new DataPlan("plan1", "test1", MYSQL, List.of(
                        TEST_HELPER.createSimpleData("t4", 10L)
                )),
                new DataPlan("plan2", "test1", MYSQL, List.of(
                        TEST_HELPER.createSimpleData("t4", 10L)
                )),
                new DataPlan("plan3", "test2", MYSQL, List.of(
                        TEST_HELPER.createSimpleDataWithId("t4", 10L, 1)
                )),
                new DataPlan("plan4", "test1", MYSQL, List.of(
                        TEST_HELPER.createSimpleData("abc", 10L)
                ))
        };
        var config = DataGenerateConfig.builder()
                .build();
        var blueprint = service.createDataBlueprint(schema, config, null, plans);
        assertEquals(1, blueprint.getEntities().size());
    }

    @Test
    void testCreateEntityMapWithReference() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        TEST_HELPER.createSimpleBlueprintData(data, idMap);
        var service = new DataPlanService(dataProviderService);
        var blueprint = new DataBlueprint();
        blueprint.getEntities().putAll(data);
        blueprint.getEntityWrapperIds().putAll(idMap);
        service.enableReferences(blueprint);
        assertEquals(1, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")).getReferenced().size());
        assertEquals(1, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")).getReferenced().size());
    }

    @Test
    void testCreateEntityReferenceFromDifferentEntities() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        var t1 = new DataEntity("t1",
                Set.of(
                        entityPropertyBuilder().name("t1p1").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t1p2").build(),
                        entityPropertyBuilder().name("t1p3").type(TEXT).constraints(List.of(new UnfixedSize(15L))).build(),
                        entityPropertyBuilder().name("t1p4").build()
                )
        );

        var t2 = new DataEntity("t2",
                Set.of(
                        entityPropertyBuilder().name("t2p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t2p1").type(TEXT).constraints(List.of(new Length(15L))).build()
                )
        );

        var t3 = new DataEntity("t3",
                Set.of(
                        entityPropertyBuilder().name("t3p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t3p1").index(entityIndexBuilder().id(1).build()).reference(new Reference("t2", "t2p0", "t2p0")).build(),
                        entityPropertyBuilder().name("t3p2").type(TEXT).index(entityIndexBuilder().id(1).build()).reference(new Reference("t2", "t2p1", "t2p0")).build()
                )
        );

        var t5 = new DataEntity("t5",
                Set.of(
                        entityPropertyBuilder().name("t5p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t5p1").index(entityIndexBuilder().id(2).build()).constraints(List.of(new Interval(1L, 0L)))
                                .reference(new Reference("t1", "t1p1", "t1p1"))
                                .build(),
                        entityPropertyBuilder().name("t5p2").index(entityIndexBuilder().id(2).build()).constraints(List.of(new Interval(1L, 0L)))
                                .reference(new Reference("t1", "t1p2", "t1p1")).build(),
                        entityPropertyBuilder().name("t5p3").type(TEXT).index(entityIndexBuilder().id(2).build())
                                .reference(new Reference("t2", "t2p1", null))
                                .build()
                )
        );

        var wrapperT1 = new EntityWrapper(t1, new EntityData("t1", 5L, null, null), null, dataProviderService, 0);
        var wrapperT2 = new EntityWrapper(t2, new EntityData("t2", 5L, null, null), null, dataProviderService, 0);
        var wrapperT3 = new EntityWrapper(t3, new EntityData("t3", 5L, null, null), null, dataProviderService, 0);
        var wrapperT5 = new EntityWrapper(t5, new EntityData("t5", 5L, null, null), null, dataProviderService, 0);

        data.put(wrapperT1.getId(), wrapperT1);
        data.put(wrapperT2.getId(), wrapperT2);
        data.put(wrapperT3.getId(), wrapperT3);
        data.put(wrapperT5.getId(), wrapperT5);
        DataHelper.appendToSetValueInMap(idMap, wrapperT1.getEntityName(), wrapperT1.getId());
        DataHelper.appendToSetValueInMap(idMap, wrapperT2.getEntityName(), wrapperT2.getId());
        DataHelper.appendToSetValueInMap(idMap, wrapperT3.getEntityName(), wrapperT3.getId());
        DataHelper.appendToSetValueInMap(idMap, wrapperT5.getEntityName(), wrapperT5.getId());

        var service = new DataPlanService(dataProviderService);
        var blueprint = new DataBlueprint();
        blueprint.getEntities().putAll(data);
        blueprint.getEntityWrapperIds().putAll(idMap);

        service.enableReferences(blueprint);
        assertEquals(2,  blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t1")).getReferenced().size());
        assertEquals(3, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")).getReferenced().size());

    }
}
