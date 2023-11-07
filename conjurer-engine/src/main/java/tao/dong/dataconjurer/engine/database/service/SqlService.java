package tao.dong.dataconjurer.engine.database.service;

import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.StringValueSupplier;
import tao.dong.dataconjurer.common.service.DataGenerateService;
import tao.dong.dataconjurer.common.service.DataPlanService;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.PropertyValueConverter;
import tao.dong.dataconjurer.engine.database.model.EntityQueryOutput;
import tao.dong.dataconjurer.engine.database.support.MySQLPropertyValueConverter;

import java.util.List;

public class SqlService {

    private final DataPlanService dataPlanService;
    private final DataGenerateService dataGenerateService;
    private final PropertyValueConverter<StringValueSupplier<String>> converter = new MySQLPropertyValueConverter();

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
//        var entities = blueprint.sortEntityByDependencies();
        return null;
    }

}
