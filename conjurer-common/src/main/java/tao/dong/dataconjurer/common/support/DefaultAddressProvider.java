package tao.dong.dataconjurer.common.support;

import org.apache.commons.collections4.MapUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.DataProviderType;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static tao.dong.dataconjurer.common.model.ConjurerDataProviderType.ADDRESS;

public class DefaultAddressProvider implements CategorizedValueProvider {
    private final JFakerValueProvider provider = new JFakerValueProvider();


    @Override
    public DataProviderType getDataProviderType() {
        return ADDRESS;
    }

    @Override
    public List<CompoundValue> fetch(int count, Locale locale, @SuppressWarnings("unused") Map<String, List<Constraint<?>>> constraints) {
        return provider.generateAddresses(count, locale);
    }
}
