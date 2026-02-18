package tao.dong.dataconjurer.shell.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataGenerateException;
import tao.dong.dataconjurer.common.support.DataGenerationErrorType;
import tao.dong.dataconjurer.engine.database.service.SqlService;
import tao.dong.dataconjurer.shell.model.MySQLDataPlan;
import tao.dong.dataconjurer.shell.service.FileOutputService;
import tao.dong.dataconjurer.shell.service.YamlFileService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConjurerCommandTest {

    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final Validator validator = mock(Validator.class);
    private final SqlService sqlService = mock(SqlService.class);
    private final DataGenerateConfig dataGenerateConfig = mock(DataGenerateConfig.class);
    private final FileOutputService fileOutputService = mock(FileOutputService.class);

    @Test
    void testConjureCommand() throws URISyntaxException, IOException {
        YamlFileService yamlFileService = mock(YamlFileService.class);

        var conjureCommand = new ConjurerCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        var mysqlPlan = new MySQLDataPlan();
        mysqlPlan.setPlan(new DataPlan("test", "test", null, Collections.emptyList()));
        when(yamlFileService.parsePlanFile(anyString())).thenReturn(mysqlPlan);
        when(yamlFileService.parseSchemaFile(anyString())).thenReturn(new DataSchema("test", Set.of(new DataEntity("t1", Set.of(new EntityProperty("p1", PropertyType.SEQUENCE, null, null, null))))));
        var cmd = new CommandLine(conjureCommand);
        cmd.setOut(new PrintWriter(printWriter));

        String schema = getFilePathForClassPathResource("schema.yaml");
        String plan = getFilePathForClassPathResource("plan.yaml");

        when(validator.validate(any(DataSchema.class))).thenReturn(Collections.emptySet());
        when(sqlService.generateSQLs(any(DataSchema.class), any(DataGenerateConfig.class), isNull(), any(DataPlan.class))).thenReturn(Collections.emptyList());
        doNothing().when(fileOutputService).generateSQLFiles(anyList());

        int exitCode = cmd.execute(schema, plan);
        assertEquals(0, exitCode);
    }

    @Test
    void testConjureCommand_GenerationFailed() throws URISyntaxException, IOException {
        YamlFileService yamlFileService = mock(YamlFileService.class);

        var conjureCommand = new ConjurerCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        var mysqlPlan = new MySQLDataPlan();
        mysqlPlan.setPlan(new DataPlan("test", "test", null, Collections.emptyList()));
        when(yamlFileService.parsePlanFile(anyString())).thenReturn(mysqlPlan);
        when(yamlFileService.parseSchemaFile(anyString())).thenReturn(new DataSchema("test", Set.of(new DataEntity("t1", Set.of(new EntityProperty("p1", PropertyType.SEQUENCE, null, null, null))))));
        var cmd = new CommandLine(conjureCommand);
        cmd.setOut(new PrintWriter(printWriter));

        String schema = getFilePathForClassPathResource("schema.yaml");
        String plan = getFilePathForClassPathResource("plan.yaml");

        when(validator.validate(any(DataSchema.class))).thenReturn(Collections.emptySet());
        when(sqlService.generateSQLs(any(DataSchema.class), any(DataGenerateConfig.class), isNull(), any(DataPlan.class))).thenThrow(new DataGenerateException(DataGenerationErrorType.MISC, "error"));

        int exitCode = cmd.execute(schema, plan);
        assertEquals(1, exitCode);
    }

    @Test
    void testConjureCommand_IOException() throws URISyntaxException, IOException {
        YamlFileService yamlFileService = mock(YamlFileService.class);

        var conjureCommand = new ConjurerCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        var mysqlPlan = new MySQLDataPlan();
        mysqlPlan.setPlan(new DataPlan("test", "test", null, Collections.emptyList()));
        when(yamlFileService.parsePlanFile(anyString())).thenThrow(new IOException("error"));
        var cmd = new CommandLine(conjureCommand);
        cmd.setOut(new PrintWriter(printWriter));

        String schema = getFilePathForClassPathResource("schema.yaml");
        String plan = getFilePathForClassPathResource("plan.yaml");

        when(validator.validate(any(DataSchema.class))).thenReturn(Collections.emptySet());

        int exitCode = cmd.execute(schema, plan);
        assertEquals(2, exitCode);
    }

    @Test
    void testOption() {
        YamlFileService yamlFileService = mock(YamlFileService.class);
        var conjureCommand = new ConjurerCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        String[] args = {"-e", "15", "-c", "200", "-i", "30", "-t", "60", "-p", "true", "schema.yaml"};
        new CommandLine(conjureCommand).parseArgs(args);
        assertEquals(15, conjureCommand.getTimeOutInMinutes());
        assertEquals(200, conjureCommand.getMaxCollision());
        assertEquals(30, conjureCommand.getGenerationInterval());
        assertEquals(60, conjureCommand.getMaxTimeout());
        assertEquals(true, conjureCommand.getPartialResult());
    }

    @Test
    void testOption_LongOption() {
        YamlFileService yamlFileService = mock(YamlFileService.class);
        var conjureCommand = new ConjurerCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        String[] args = {"--entity-timeout", "15", "--max-collision", "200", "--wait-interval", "30", "--timeout", "60", "--partial-result", "true", "schema.yaml"};
        new CommandLine(conjureCommand).parseArgs(args);
        assertEquals(15, conjureCommand.getTimeOutInMinutes());
        assertEquals(200, conjureCommand.getMaxCollision());
        assertEquals(30, conjureCommand.getGenerationInterval());
        assertEquals(60, conjureCommand.getMaxTimeout());
        assertEquals(true, conjureCommand.getPartialResult());
    }

    @Test
    void testValidateInput() {
        YamlFileService yamlFileService = mock(YamlFileService.class);
        var conjureCommand = new ConjurerCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        @SuppressWarnings("unchecked")
        ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("error");
        when(validator.validate("Error")).thenReturn(Set.of(violation));
        assertThrows(CommandLine.TypeConversionException.class, () -> conjureCommand.validateInput("Error", "field", Path.of("test")));
    }


    private String getFilePathForClassPathResource(String resource) throws URISyntaxException {
        return Path.of(ClassLoader.getSystemResource(resource).toURI()).toString();
    }
}
