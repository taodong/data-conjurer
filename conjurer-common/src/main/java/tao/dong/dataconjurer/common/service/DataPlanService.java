package tao.dong.dataconjurer.common.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.SequenceGenerator;
import tao.dong.dataconjurer.common.support.SequenceGeneratorDecorator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DataPlanService {
    public DataBlueprint createDataBlueprint(DataSchema schema, DataGenerateConfig config, DataPlan... dataPlans) {
        var blueprint = new DataBlueprint();
        var entityDefinitions = extraEntityDefinitions(schema);

        for (var dataPlan : dataPlans) {
            if (StringUtils.equals(schema.name(), dataPlan.schema()))
                constructBlueprint(blueprint, entityDefinitions, dataPlan);
            else {
                LOG.warn("Data plan {} is ignored due to target schema {} doesn't match current schema {}", dataPlan.name(), dataPlan.schema(), schema.name());
            }
        }

        if (!blueprint.getEntities().isEmpty()) {
            enableReferences(blueprint);
            balanceIndex(blueprint, config);
        }

        return blueprint;
    }

    private void balanceIndex(DataBlueprint blueprint, DataGenerateConfig config) {
        for (var wrapperIds : blueprint.getEntityWrapperIds().values()) {
            if (wrapperIds.size() > 1) {
                EntityWrapperId previous = null;
                for (var current : wrapperIds) {
                    if (previous != null) {
                        updatePropertyGenerators(config, blueprint.getEntities().get(previous), blueprint.getEntities().get(current));
                    }
                    previous = current;
                }
            }
        }
    }

    void updatePropertyGenerators(DataGenerateConfig config, EntityWrapper previous, EntityWrapper current) {
        for (var property : previous.getEntity().properties()) {
            if (property.idIndex() != -1) {
                var generator = previous.getGenerators().get(property.name());
                if (generator instanceof SequenceGeneratorDecorator prevGen) {
                    ((SequenceGeneratorDecorator)(current.getGenerators().get(property.name())))
                            .getGenerator()
                            .setCurrent(calculateNewBase(prevGen.getGenerator(), previous.getCount() + config.getMaxIndexCollision()));
                }
            }
        }
    }

    private long calculateNewBase(SequenceGenerator generator, long rounds) {
        return generator.calculateGeneratedValue(rounds) + generator.getLeap();
    }

    void enableReferences(DataBlueprint blueprint) {
        var refs = new HashMap<String, Set<String>>();
        for (var entityEntry : blueprint.getEntities().values()) {
            for (var ref : entityEntry.getReferences().values()) {
                DataHelper.appendToSetValueInMap(refs, ref.entity(), ref.property());
            }
        }
        for (var entry : refs.entrySet()) {
            var entityIds = blueprint.getEntityWrapperIds().get(entry.getKey());
            if (CollectionUtils.isNotEmpty(entityIds)) {
                entityIds.forEach(
                        id -> blueprint.getEntities().get(id).createReferenced(entry.getValue().toArray(String[]::new))
                );
            }
        }
    }

    private void constructBlueprint(DataBlueprint blueprint, Map<String, DataEntity> entityDefinitions, DataPlan dataPlan) {
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
