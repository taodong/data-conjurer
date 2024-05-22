package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.StringFormat;
import tao.dong.dataconjurer.common.model.TextId;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultIdProviderTest {
    private final DefaultIdProvider provider = new DefaultIdProvider();

    private static Stream<Arguments> testGenerateUUIDs() {
        return Stream.of(
                Arguments.of(Collections.emptyMap()),
                Arguments.of(Map.of("value", List.of(new Length(10L)))),
                Arguments.of(Map.of("UUId", List.of(new Length(10L))))
        );
    }

    @ParameterizedTest
    @MethodSource("testGenerateUUIDs")
    void testGenerateUUIDs(Map<String, List<Constraint<?>>> constraints) {
        var ids = provider.fetch(10, null, constraints);
        assertEquals(10, ids.size());
        for (var id : ids) {
            String uuidStr = ((TextId)id).value();
            assertEquals(UUID.fromString(uuidStr).toString(), uuidStr);
        }
    }

    @Test
    void testGetDataProviderType() {
        assertEquals("ID", provider.getDataProviderType());
    }

    @Test
    void testGenerateRegexIds() {
        var ids = provider.fetch(9, null, Map.of("value", List.of(new Length(6L), new StringFormat("id-###"))));
        assertEquals(9, ids.size());
        for (var id : ids) {
            String idStr = ((TextId)id).value();
            assertTrue(Pattern.matches("id-\\d{3}", idStr));
        }
    }
}
