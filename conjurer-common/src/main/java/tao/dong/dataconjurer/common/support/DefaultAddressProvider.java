package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.Constraint;

import java.util.List;
import java.util.Locale;

public class DefaultAddressProvider implements CategorizedStringValueProvider {
    private final JFakerValueProvider provider = new JFakerValueProvider();

    @Override
    public List<String> fetch(int count, String qualifier, Locale locale, @SuppressWarnings("unused") List<Constraint<?>> constraints) {
        return provider.generateAddresses(count, qualifier, locale);
    }
}
