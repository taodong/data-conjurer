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
                Arguments.of(2, null),
                Arguments.of(3, Locale.JAPAN),
                Arguments.of(4, Locale.FRANCE),
                Arguments.of(5, null),
                Arguments.of(6, Locale.CHINA),
                Arguments.of(7, Locale.CANADA),
                Arguments.of(8, Locale.US)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerateAddresses(int count, Locale locale) {
        assertEquals(count, provider.generateAddresses(count, locale).size());
    }

    private static Stream<Arguments> testGenerateNames() {
        return Stream.of(
                Arguments.of(1, null),
                Arguments.of(5, Locale.forLanguageTag("zh-tw")),
                Arguments.of(3, Locale.JAPANESE),
                Arguments.of(7, Locale.US)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerateNames(int count, Locale locale) {
        assertEquals(count, provider.generateNames(count, locale).size());
    }

}
