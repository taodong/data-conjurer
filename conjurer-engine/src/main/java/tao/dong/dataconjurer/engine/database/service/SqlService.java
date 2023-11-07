package tao.dong.dataconjurer.engine.database.service;

import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.service.DataGenerateService;
import tao.dong.dataconjurer.common.service.DataPlanService;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.engine.database.model.EntityQueryOutput;

import java.util.List;

public class SqlService {

    private final DataPlanService dataPlanService;
    private final DataGenerateService dataGenerateService;

    public SqlService(DataPlanService dataPlanService, DataGenerateService dataGenerateService) {
        this.dataPlanService = dataPlanService;
        this.dataGenerateService = dataGenerateService;
    }


    public List<EntityQueryOutput> generateSQLs(DataSchema schema, DataGenerateConfig config, DataPlan dataPlan) {
        
        return null;
    }

    private List<EntityQueryOutput> createInsertStatements(DataSchema schema, DataGenerateConfig config, DataPlan dataPlan) {
        var blueprint = dataPlanService.createDataBlueprint(schema, config, dataPlan);
        dataGenerateService.generateData(blueprint, config);

        return null;
    }

}
