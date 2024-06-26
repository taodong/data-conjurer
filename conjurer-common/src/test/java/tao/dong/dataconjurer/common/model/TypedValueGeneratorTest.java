package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.support.BigDecimalGenerator;
import tao.dong.dataconjurer.common.support.DateGenerator;
import tao.dong.dataconjurer.common.support.DatetimeGenerator;
import tao.dong.dataconjurer.common.support.ElectedValueSelector;
import tao.dong.dataconjurer.common.support.EntityTestHelper;
import tao.dong.dataconjurer.common.support.FormattedTextGenerator;
import tao.dong.dataconjurer.common.support.MutableSequenceGenerator;
import tao.dong.dataconjurer.common.support.NumberCalculator;
import tao.dong.dataconjurer.common.support.StringTransformer;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class TypedValueGeneratorTest {
    private final TypedValueGenerator generator = mock(TypedValueGenerator.class, CALLS_REAL_METHODS);

    private static Stream<Arguments> testMatchDefaultGeneratorByType() {
        return Stream.of(
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.TEXT)
                                .build(),
                        FormattedTextGenerator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.TEXT)
                                .constraints(
                                        List.of(
                                                new StringAlternation(Set.of("p1"), "p1")
                                        )
                                )
                                .build(),
                        StringTransformer.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.SEQUENCE)
                                .build(),
                        MutableSequenceGenerator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.NUMBER)
                                .build(),
                        BigDecimalGenerator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.NUMBER)
                                .constraints(
                                        List.of(
                                              new NumberCorrelation(Set.of("p1"), "pow(p1)")
                                        )
                                )
                                .build(),
                        NumberCalculator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.DATE)
                                .build(),
                        DateGenerator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.DATE)
                                .constraints(
                                        List.of(
                                                new NumberCorrelation(Set.of("p1"), "PAST_TIME_AFTER(p1)")
                                        )
                                )
                                .build(),
                        NumberCalculator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.DATETIME)
                                .build(),
                        DatetimeGenerator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.DATETIME)
                                .constraints(
                                        List.of(
                                                new NumberCorrelation(Set.of("p1"), "PAST_TIME_AFTER(p1)")
                                        )
                                )
                                .build(),
                        NumberCalculator.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.BOOLEAN)
                                .build(),
                        ElectedValueSelector.class
                ),
                Arguments.of(
                        EntityTestHelper.entityPropertyBuilder()
                                .type(PropertyType.TIME)
                                .build(),
                        tao.dong.dataconjurer.common.support.TimeGenerator.class
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testMatchDefaultGeneratorByType")
    void testMatchDefaultGeneratorByType(EntityProperty property, Class<?> expected) {
        var result = generator.matchDefaultGeneratorByType(property, null);
        assertTrue(expected.isInstance(result));
    }

    @Test
    void testFilterConstraintByType() {
        var constraints = List.of(
                new NumberCorrelation(Set.of("p1"), "pow(p1)"),
                new StringAlternation(Set.of("p1"), "p1"),
                new Length(10L)
        );
        var result = TypedValueGenerator.filterConstraintByType(constraints, ConstraintType.CORRELATION);
        assertTrue(result instanceof NumberCorrelation);
    }
}
