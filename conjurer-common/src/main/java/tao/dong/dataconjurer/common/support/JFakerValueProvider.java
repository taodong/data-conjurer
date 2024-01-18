package tao.dong.dataconjurer.common.support;

import jakarta.validation.constraints.Min;
import net.datafaker.Faker;
import tao.dong.dataconjurer.common.model.Address;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.PersonName;
import tao.dong.dataconjurer.common.model.TextId;

import java.util.List;
import java.util.Locale;

public class JFakerValueProvider implements LocaleValueCollector {

    public List<CompoundValue> generateNames(@Min(1) int count, Locale locale) {
        final var faker = createFaker(locale);
        return collect(count, () -> new PersonName(faker.name().name(), faker.name().firstName(), faker.name().lastName()));
    }

    public List<CompoundValue> generateAddresses(@Min(1) int count, Locale locale) {
        final var faker = createFaker(locale);
        return collect(count, () -> new Address(faker.address().fullAddress(), faker.address().streetAddress(), faker.address().city(),
                faker.address().state(), faker.address().zipCode(), locale == null ? faker.address().country() : locale.getDisplayCountry(locale)));
    }

    public List<CompoundValue> generateExpressionValues(@Min(1) int count, String template) {
        final var faker = createFaker(null);
        return collect(count, () -> new TextId(faker.bothify(template)));
    }


    private Faker createFaker(Locale locale) {
        return locale == null? new Faker() : new Faker(locale);
    }
}
