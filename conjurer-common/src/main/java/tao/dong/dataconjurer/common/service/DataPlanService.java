package tao.dong.dataconjurer.common.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DataPlanService {
    public DataBlueprint createDataBlueprint(DataSchema schema, DataPlan... dataPlans) {
        var blueprint = new DataBlueprint();
        var entityDefinitions = extraEntityDefinitions(schema);

        for (var dataPlan : dataPlans) {
            if (StringUtils.equals(schema.name(), dataPlan.schema())) {
                fillBlueprint(blueprint, entityDefinitions, dataPlan);
            } else {
                LOG.warn("Data plan {} is ignored due to target schema {} doesn't match current schema {}", dataPlan.name(), dataPlan.schema(), schema.name());
            }
        }

        return blueprint;
    }

    private void fillBlueprint(DataBlueprint blueprint, Map<String, DataEntity> entityDefinitions, DataPlan dataPlan) {
        var entityWrapperMap = blueprint.getEntities();
        var entityWrapperIdMap = blueprint.getEntityWrapperIds();
        for (var entityData : dataPlan.data()) {
            var dataEntity = entityDefinitions.get(entityData.entity());
            if (dataEntity != null) {
                var wrapperId = new EntityWrapperId(dataEntity.name(), entityData.dataId());
                if (!entityWrapperMap.containsKey(wrapperId)) {
                    DataHelper.appendToSetValueInMap(entityWrapperIdMap, dataEntity.name(), wrapperId);
                    entityWrapperMap.put(wrapperId, new EntityWrapper(dataEntity, entityData));
                } else {
                    LOG.warn("Duplicated id found for plan {} entity {}", dataPlan.name(), entityData.entity());
                }
            } else {
                LOG.warn("Targeted entity {} isn't found in data plan {}", entityData.entity(), dataPlan.name());
            }
        }
    }

    private Map<String, DataEntity> extraEntityDefinitions(DataSchema schema) {
        return schema.entities().stream().collect(Collectors.toMap(DataEntity::name, Function.identity()));
    }

}
