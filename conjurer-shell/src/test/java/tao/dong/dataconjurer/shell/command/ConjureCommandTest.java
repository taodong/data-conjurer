package tao.dong.dataconjurer.shell.command;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConjureCommandTest {

    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final Validator validator = mock(Validator.class);
    private final SqlService sqlService = mock(SqlService.class);
    private final DataGenerateConfig dataGenerateConfig = mock(DataGenerateConfig.class);
    private final FileOutputService fileOutputService = mock(FileOutputService.class);

    @Test
    void testConjureCommand() throws URISyntaxException, IOException {
        YamlFileService yamlFileService = mock(YamlFileService.class);

        var conjureCommand = new ConjureCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        var mysqlPlan = new MySQLDataPlan();
        mysqlPlan.setPlan(new DataPlan("test", "test", null, Collections.emptyList()));
        when(yamlFileService.parsePlanFile(anyString())).thenReturn(mysqlPlan);
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

    private String getFilePathForClassPathResource(String resource) throws URISyntaxException {
        return Path.of(ClassLoader.getSystemResource(resource).toURI()).toString();
    }
}
