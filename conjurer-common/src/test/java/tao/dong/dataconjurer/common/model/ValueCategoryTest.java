package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.model.ConstraintType.CATEGORY;

class ValueCategoryTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testJacksonDeserialize() {
        return Stream.of(
                Arguments.of("{\"type\": \"category\", \"name\": \"name\"}", "name", null, null),
                Arguments.of("{\"type\": \"category\", \"name\": \"address\", \"locale\": \"en-US-POSIX\"}", "address", null, Locale.forLanguageTag("en-US-POSIX")),
                Arguments.of("{\"type\": \"category\", \"name\": \"name\", \"locale\": \"zh-CN\", \"qualifier\": \"firstname\"}", "name", "firstname", Locale.CHINA)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testJacksonDeserialize(String json, String name, String qualifier, Locale locale) throws JsonProcessingException {
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
