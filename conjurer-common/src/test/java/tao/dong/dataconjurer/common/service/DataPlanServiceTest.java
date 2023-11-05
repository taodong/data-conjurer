package tao.dong.dataconjurer.common.service;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataPlanServiceTest {

    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();

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
