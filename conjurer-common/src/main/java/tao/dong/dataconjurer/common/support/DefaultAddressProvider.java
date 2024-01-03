package tao.dong.dataconjurer.common.support;

import org.apache.commons.collections4.MapUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DefaultAddressProvider implements CategorizedValueProvider {
    private final JFakerValueProvider provider = new JFakerValueProvider();


    @Override
    public List<CompoundValue> fetch(int count, Locale locale, @SuppressWarnings("unused") Map<String, List<Constraint<?>>> constraints) {
        return provider.generateAddresses(count, locale);
    }
}
