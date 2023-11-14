package tao.dong.dataconjurer.engine.database.service;

import tao.dong.dataconjurer.common.model.DataOutputControl;
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
import java.util.stream.IntStream;

public class SqlService {

    private final DataPlanService dataPlanService;
    private final DataGenerateService dataGenerateService;
    private final InsertStatementService insertStatementService;
    private final PropertyValueConverter<StringValueSupplier<String>> converter = new MySQLPropertyValueConverter();

    public SqlService(DataPlanService dataPlanService, DataGenerateService dataGenerateService, InsertStatementService insertStatementService) {
        this.dataPlanService = dataPlanService;
        this.dataGenerateService = dataGenerateService;
        this.insertStatementService = insertStatementService;
    }


    public List<EntityQueryOutput> generateSQLs(DataSchema schema, DataGenerateConfig config, DataOutputControl outputControl, DataPlan dataPlan) {
        
        return createInsertStatements(schema, config, outputControl, dataPlan);
    }

    private List<EntityQueryOutput> createInsertStatements(DataSchema schema, DataGenerateConfig config, DataOutputControl outputControl, DataPlan dataPlan) {
        var blueprint = dataPlanService.createDataBlueprint(schema, config, outputControl, dataPlan);
        dataGenerateService.generateData(blueprint, config);
        var generated = blueprint.outputGeneratedData();
        return IntStream.range(0, generated.size())
                .mapToObj(i -> EntityQueryOutput.builder()
                        .order(i)
                        .entity(generated.get(i).getEntityName())
                        .queries(
                             insertStatementService.generateInsertStatement(
                                     generated.get(i).getEntityName(),
                                     generated.get(i).getProperties(),
                                     converter.convertRecords(generated.get(i).getValues(), generated.get(i).getPropertyTypes())
                             )
                        )
                        .build()
                )
                .toList();
    }
}
