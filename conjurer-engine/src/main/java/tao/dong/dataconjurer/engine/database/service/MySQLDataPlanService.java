package tao.dong.dataconjurer.engine.database.service;

import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityOutputControl;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.service.DataPlanService;
import tao.dong.dataconjurer.engine.database.model.MySQLEntityWrapper;

public class MySQLDataPlanService extends DataPlanService {
    @Override
    protected EntityWrapper createEntityWrapper(DataEntity dataEntity, EntityData entityData, EntityOutputControl control) {
        return new MySQLEntityWrapper(dataEntity, entityData, control);
    }
}
