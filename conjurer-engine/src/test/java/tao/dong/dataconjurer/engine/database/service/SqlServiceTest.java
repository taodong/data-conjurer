package tao.dong.dataconjurer.engine.database.service;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.DataOutputControl;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.Dialect;
import tao.dong.dataconjurer.common.model.EntityDataOutput;
import tao.dong.dataconjurer.common.service.DataGenerateService;
import tao.dong.dataconjurer.common.service.DataPlanService;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

class SqlServiceTest {
    private final DataPlanService dataPlanService = mock(DataPlanService.class);
    private final DataGenerateService dataGenerateService = mock(DataGenerateService.class);
    private final InsertStatementService insertStatementService = mock(InsertStatementService.class);
    private final SqlService sqlService = new SqlService(dataPlanService, dataGenerateService, insertStatementService);

    @Test
    void testGenerateSQLs() {
        DataSchema schema = new DataSchema("test", Collections.emptySet());
        DataGenerateConfig generateConfig = mock(DataGenerateConfig.class);
        DataOutputControl outputControl = new DataOutputControl("control", Collections.emptySet());
        DataPlan dataPlan = new DataPlan("plan", "test", Dialect.MYSQL, Collections.emptyList());
        DataBlueprint blueprint = mock(DataBlueprint.class);
        when(dataPlanService.createDataBlueprint(any(DataSchema.class), any(DataGenerateConfig.class), any(DataOutputControl.class), any(DataPlan.class)))
                .thenReturn(blueprint);
        doNothing().when(dataGenerateService).generateData(any(DataBlueprint.class), any(DataGenerateConfig.class));
        var entity1 = new EntityDataOutput("t1", List.of(SEQUENCE), List.of("id"));
        entity1.addValues(List.of(
                List.of(1L), List.of(2L)
        ));
        var entityData = List.of(entity1);
        when(blueprint.outputGeneratedData()).thenReturn(entityData);
        when(insertStatementService.generateInsertStatement(eq("t1"), anyList(), anyList())).thenReturn(new StringBuilder("t1_query"));
        var result = sqlService.generateSQLs(schema, generateConfig, outputControl, dataPlan);
        assertEquals(1, result.size());
        assertEquals("t1", result.get(0).getEntity());
        assertEquals(0, result.get(0).getOrder());
    }

}
