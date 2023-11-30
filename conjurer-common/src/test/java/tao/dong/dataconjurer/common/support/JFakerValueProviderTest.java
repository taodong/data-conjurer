package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JFakerValueProviderTest {
    private final JFakerValueProvider provider = new JFakerValueProvider();

    private static Stream<Arguments> testGenerateAddresses() {
        return Stream.of(
                Arguments.of(2, null, null),
                Arguments.of(3, "street", Locale.JAPAN),
                Arguments.of(4, "city", Locale.FRANCE),
                Arguments.of(5, "country", null),
                Arguments.of(6, "zip", Locale.CHINA),
                Arguments.of(7, "address", Locale.CANADA)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerateAddresses(int count, String qualifier, Locale locale) {
        assertEquals(count, provider.generateAddresses(count, qualifier, locale).size());
    }

    private static Stream<Arguments> testGenerateNames() {
        return Stream.of(
                Arguments.of(1, null, null),
                Arguments.of(5, "firstname", new Locale("zh", "tw")),
                Arguments.of(3, "lastname", Locale.JAPANESE),
                Arguments.of(7, "abc", Locale.US)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerateNames(int count, String qualifier, Locale locale) {
        assertEquals(count, provider.generateNames(count, qualifier, locale).size());
    }

}
