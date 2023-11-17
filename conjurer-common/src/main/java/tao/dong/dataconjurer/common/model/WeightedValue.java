package tao.dong.dataconjurer.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import tao.dong.dataconjurer.common.support.ElectedValueSelector;

@Data
@AllArgsConstructor
public class WeightedValue {
    private ElectedValueSelector selector;
    private RatioRange weighRange;
}
