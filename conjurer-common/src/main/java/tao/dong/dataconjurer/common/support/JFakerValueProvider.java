package tao.dong.dataconjurer.common.support;

import com.github.javafaker.Faker;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.support.LocaleStringValueCollector;

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

    private Faker createFaker(Locale locale) {
        return locale == null? new Faker() : new Faker(locale);
    }

    private String normalizeQualifier(String qualifier) {
        return Optional.ofNullable(qualifier).map(StringUtils::lowerCase).orElse("");
    }
}
