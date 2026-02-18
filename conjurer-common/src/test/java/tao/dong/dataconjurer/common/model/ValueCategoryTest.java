package tao.dong.dataconjurer.common.model;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.model.ConstraintType.CATEGORY;

class ValueCategoryTest {
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    private static Stream<Arguments> testJacksonDeserialize() {
        return Stream.of(
                Arguments.of("{\"type\": \"category\", \"name\": \"name\"}", "name", null, null),
                Arguments.of("{\"type\": \"category\", \"name\": \"address\", \"locale\": \"en-US-POSIX\"}", "address", null, Locale.forLanguageTag("en-US-POSIX")),
                Arguments.of("{\"type\": \"category\", \"name\": \"name\", \"locale\": \"zh-CN\", \"qualifier\": \"firstname\"}", "name", "firstname", Locale.CHINA)
        );
    }

    @ParameterizedTest
    @MethodSource("testJacksonDeserialize")
    void testJacksonDeserialize(String json, String name, String qualifier, Locale locale) throws JacksonException {
        ValueCategory category = objectMapper.readerFor(ValueCategory.class).readValue(json);
        assertEquals(new ValueCategory(name, qualifier, locale, 0), category);
    }

    @Test
    void testIsMet() {
        var test = new ValueCategory("abc", null, null, 0);
        assertTrue(test.isMet("abc"));
    }

    @Test
    void testGetType() {
        var test = new ValueCategory("abc", null, null, 0);
        assertEquals(CATEGORY, test.getType());
    }

    @Test
    void testGetValueId() {
        var test = new ValueCategory("abc", null, null, 0);
        assertEquals("abc_0", test.getValueId());
    }
}
