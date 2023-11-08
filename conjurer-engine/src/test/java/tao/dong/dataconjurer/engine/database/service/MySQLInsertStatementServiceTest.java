package tao.dong.dataconjurer.engine.database.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.StringValueSupplier;
import tao.dong.dataconjurer.engine.database.model.MySQLTextValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySQLInsertStatementServiceTest {
    private final MySQLInsertStatementService service = new MySQLInsertStatementService();

    private static Stream<Arguments> testGenerateInsertStatement() {
        return Stream.of(
                Arguments.of("city",
                             List.of("name", "country"),
                             List.of(List.of(new MySQLTextValue("new york"), new MySQLTextValue("usa"))),
                             "INSERT INTO city(name,country) VALUES ('new york','usa');"
                ),
                Arguments.of("city",
                             List.of("name", "country"),
                             List.of(List.of(new MySQLTextValue("new york"), new MySQLTextValue("usa")),
                                     List.of(new MySQLTextValue("dc"), new MySQLTextValue("usa"))),
                             "INSERT INTO city(name,country) VALUES ('new york','usa'); INSERT INTO city(name,country) VALUES ('dc','usa');"
                ),
                Arguments.of("city",
                             List.of("name", "country"),
                             List.of(List.of(new MySQLTextValue("new york"), new MySQLTextValue("usa")),
                                     Arrays.asList(new MySQLTextValue("dc"), null)),
                                     "INSERT INTO city(name,country) VALUES ('new york','usa'); INSERT INTO city(name) VALUES ('dc');"
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerateInsertStatement(String entity, List<String> properties, List<List<StringValueSupplier<String>>> values, String expected) {
        assertEquals(expected, service.generateInsertStatement(entity, properties, values).toString());
    }
}
