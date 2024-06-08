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
        return collect(count, () -> {
            var name = faker.name();
            return new PersonName(name.name(), name.firstName(), name.lastName());
        });
    }

    public List<CompoundValue> generateAddresses(@Min(1) int count, Locale locale) {
        final var faker = createFaker(locale);

        return collect(count, () -> {
            var address = faker.address();
            String stateZip = null;
            try {
                var stateAbbr = address.stateAbbr();
                stateZip = address.zipCodeByState(stateAbbr);
            } catch (Exception e) {
                // Ignore runtime exception
            }
            return new Address(address.fullAddress(), address.streetAddress(true), address.city(),
                address.state(), stateZip == null ? address.zipCode() : stateZip, locale == null ? address.country() : locale.getDisplayCountry(locale));
        });
    }

    public List<CompoundValue> generateExpressionValues(@Min(1) int count, String template) {
        final var faker = createFaker(null);
        return collect(count, () -> new TextId(faker.bothify(template)));
    }


    private Faker createFaker(Locale locale) {
        return locale == null? new Faker() : new Faker(locale);
    }
}
