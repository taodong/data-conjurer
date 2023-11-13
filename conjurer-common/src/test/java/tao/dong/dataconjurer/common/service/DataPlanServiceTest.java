package tao.dong.dataconjurer.common.service;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.Dialect.MYSQL;

class DataPlanServiceTest {

    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();

    @Test
    void testCreateDataBlueprint() {
        var service = new DataPlanService();
        var schema = TEST_HELPER.createSimpleTestSchema();
        var plans = TEST_HELPER.createSimpleTestPlans();
        var config = DataGenerateConfig.builder()
                .maxIndexCollision(3)
                .build();
        var blueprint = service.createDataBlueprint(schema, config, plans);
        assertEquals(5, blueprint.getEntities().size());
        assertEquals(11L, blueprint.getEntities().get(new EntityWrapperId("t3", 1)).getGenerators().get("t3p0").generate());
    }

    @Test
    void testCreateDataBlueprint_IgnorePlanWithDuplicateId() {
        var service = new DataPlanService();
        var schema = TEST_HELPER.createSimpleTestSchema();
        var plans = new DataPlan[]{
                new DataPlan("plan1", "test1", MYSQL, List.of(
                        TEST_HELPER.createSimpleData("t4", 10L)
                )),
                new DataPlan("plan2", "test1", MYSQL, List.of(
                        TEST_HELPER.createSimpleData("t4", 10L)
                ))
        };
        var config = DataGenerateConfig.builder()
                .build();
        var blueprint = service.createDataBlueprint(schema, config, plans);
        assertEquals(1, blueprint.getEntities().size());
    }

    @Test
    void testCreateEntityMapWithReference() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        TEST_HELPER.createSimpleBlueprintData(data, idMap);
        var service = new DataPlanService();
        var blueprint = new DataBlueprint();
        blueprint.getEntities().putAll(data);
        blueprint.getEntityWrapperIds().putAll(idMap);
        service.enableReferences(blueprint);
        assertEquals(1, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")).getReferenced().size());
        assertEquals(1, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")).getReferenced().size());
    }
}
