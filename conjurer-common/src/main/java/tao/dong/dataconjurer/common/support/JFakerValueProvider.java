package tao.dong.dataconjurer.common.support;

import com.github.javafaker.Faker;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class JFakerValueProvider implements LocaleStringValueCollector {

    public List<String> generateNames(@Min(1) int count, String qualifier, Locale locale) {
        final var faker = createFaker(locale);
        return collect(count, () -> switch (normalizeQualifier(qualifier)) {
            case "firstname" -> faker.name().firstName();
            case "lastname" -> faker.name().lastName();
            default -> faker.name().name();
        });
    }

    public List<String> generateAddresses(@Min(1) int count, String qualifier, Locale locale) {
        final var faker = createFaker(locale);
        return collect(count, () -> switch (normalizeQualifier(qualifier)) {
            case "street" -> faker.address().streetAddress();
            case "city" -> faker.address().city();
            case "country" -> faker.address().country();
            case "state" -> faker.address().state();
            case "zip" -> faker.address().zipCode();
            default -> faker.address().fullAddress();
        });
    }


    private Faker createFaker(Locale locale) {
        return locale == null? new Faker() : new Faker(locale);
    }

    private String normalizeQualifier(String qualifier) {
        return Optional.ofNullable(qualifier).map(StringUtils::lowerCase).orElse("");
    }
}
