package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.Min;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.Address;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.PersonName;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class JFakerValueProvider implements LocaleValueCollector {

    public List<CompoundValue> generateNames(@Min(1) int count, Locale locale) {
        final var faker = createFaker(locale);
        return collect(count, () -> new PersonName(faker.name().name(), faker.name().firstName(), faker.name().lastName()));
    }

    public List<CompoundValue> generateAddresses(@Min(1) int count, Locale locale) {
        final var faker = createFaker(locale);
        return collect(count, () -> new Address(faker.address().fullAddress(), faker.address().streetAddress(), faker.address().city(),
                faker.address().state(), faker.address().zipCode(), faker.address().country()));
    }


    private Faker createFaker(Locale locale) {
        return locale == null? new Faker() : new Faker(locale);
    }

    private String normalizeQualifier(String qualifier) {
        return Optional.ofNullable(qualifier).map(StringUtils::lowerCase).orElse("");
    }
}
