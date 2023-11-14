package tao.dong.dataconjurer.shell.model;

import lombok.Data;
import tao.dong.dataconjurer.common.model.DataOutputControl;
import tao.dong.dataconjurer.common.model.DataPlan;

@Data
public class MySQLDataPlan {
    private DataPlan dataPlan;
    private DataOutputControl output;
}
