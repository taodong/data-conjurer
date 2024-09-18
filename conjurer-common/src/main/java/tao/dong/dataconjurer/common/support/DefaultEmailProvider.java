package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Email;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DefaultEmailProvider implements CategorizedValueProvider, LocaleValueCollector {
    private final RandomEmailGenerator generator = new RandomEmailGenerator();

    @Override
    public String getDataProviderType() {
        return "EMAIL";
    }

    @Override
    public List<CompoundValue> fetch(int count, @SuppressWarnings("unused") Locale locale,
                                     @SuppressWarnings("unused") Map<String, List<Constraint<?>>> constraints) {
        return collect(count, () -> new Email(StringUtils.lowerCase(generator.generate())));
    }
}
