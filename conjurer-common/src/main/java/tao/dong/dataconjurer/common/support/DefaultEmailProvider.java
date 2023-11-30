package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.api.CategorizedStringValueProvider;
import tao.dong.dataconjurer.common.model.Constraint;

import java.util.List;
import java.util.Locale;

public class DefaultEmailProvider implements CategorizedStringValueProvider, LocaleStringValueCollector {
    private final RandomEmailGenerator generator = new RandomEmailGenerator();

    @Override
    public List<String> fetch(int count, @SuppressWarnings("unused") String qualifier, @SuppressWarnings("unused") Locale locale,
                              @SuppressWarnings("unused") List<Constraint<?>> constraints) {
        return collect(count, generator::generate);
    }
}
