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
import static tao.dong.dataconjurer.common.model.Dialect.MYSQL;

class DataPlanTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        var nullData = new ArrayList<EntityData>();
        nullData.add(null);
        return Stream.of(
          Arguments.of(new DataPlan(null, "abc", MYSQL, List.of(new EntityData("t1", 1L, null))), false, "Plan name is required"),
          Arguments.of(new DataPlan(" ", "abc", MYSQL, List.of(new EntityData("t1", 1L, null))), false, "Plan name is required"),
          Arguments.of(new DataPlan("afds", " ", MYSQL, List.of(new EntityData("t1", 1L, null))), false, "Schema is required"),
          Arguments.of(new DataPlan("afds", null, MYSQL, List.of(new EntityData("t1", 1L, null))), false, "Schema is required"),
          Arguments.of(new DataPlan("afds", "fds", MYSQL, null), false, "Data must not be empty"),
          Arguments.of(new DataPlan("afds", "fds", MYSQL, Collections.emptyList()), false, "Data must not be empty"),
          Arguments.of(new DataPlan("afds", "fds", MYSQL, nullData), false, "data element can't be null"),
          Arguments.of(new DataPlan("afds", "fds", MYSQL, List.of(new EntityData("t1", 1L, null))), true, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(DataPlan plan, boolean passed, String errMsg) {
        var violations = validator.validate(plan);
        assertEquals(passed, violations.isEmpty());
        if (!passed && violations.stream().findFirst().isPresent()) {
            assertEquals(errMsg, violations.stream().findFirst().get().getMessage());
        }
    }
}
