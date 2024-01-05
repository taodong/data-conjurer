package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.Min;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface CategorizedValueProvider {

    String getDataProviderType();

    List<? extends CompoundValue> fetch(@Min(1) int count, Locale locale, Map<String, List<Constraint<?>>> constraints);
}
