package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataPlanTest {
    private static Validator validator;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        var nullData = new ArrayList<EntityData>();
        nullData.add(null);
        return Stream.of(
          Arguments.of(new DataPlan(null, "abc", List.of(new EntityData("t1", 1L))), false, "Plan name is required"),
          Arguments.of(new DataPlan(" ", "abc", List.of(new EntityData("t1", 1L))), false, "Plan name is required"),
          Arguments.of(new DataPlan("afds", " ", List.of(new EntityData("t1", 1L))), false, "Schema is required"),
          Arguments.of(new DataPlan("afds", null, List.of(new EntityData("t1", 1L))), false, "Schema is required"),
          Arguments.of(new DataPlan("afds", "fds", null), false, "Data must not be empty"),
          Arguments.of(new DataPlan("afds", "fds", Collections.emptyList()), false, "Data must not be empty"),
          Arguments.of(new DataPlan("afds", "fds", nullData), false, "data element can't be null"),
          Arguments.of(new DataPlan("afds", "fds", List.of(new EntityData("t1", 1L))), true, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(DataPlan plan, boolean passed, String errMsg) {
        var violations = validator.validate(plan);
        assertEquals(passed, violations.isEmpty());
        if (!passed) {
            assertEquals(errMsg, violations.stream().findFirst().get().getMessage());
        }
    }
}
