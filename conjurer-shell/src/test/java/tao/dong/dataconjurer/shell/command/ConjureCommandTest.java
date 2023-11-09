package tao.dong.dataconjurer.shell.command;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.engine.database.service.SqlService;
import tao.dong.dataconjurer.shell.service.FileOutputService;
import tao.dong.dataconjurer.shell.service.YamlFileService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConjureCommandTest {

    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final Validator validator = mock(Validator.class);
    private final SqlService sqlService = mock(SqlService.class);
    private final DataGenerateConfig dataGenerateConfig = mock(DataGenerateConfig.class);
    private final FileOutputService fileOutputService = mock(FileOutputService.class);

    @Test
    void testConjureCommand() throws URISyntaxException {
        YamlFileService yamlFileService = mock(YamlFileService.class);
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        var conjureCommand = new ConjureCommand(yamlFileService, validator, sqlService, dataGenerateConfig, fileOutputService);
        var cmd = new CommandLine(conjureCommand);
        cmd.setOut(new PrintWriter(printWriter));

        String schema = getFilePathForClassPathResource("schema.yaml");
        String plan = getFilePathForClassPathResource("plan.yaml");

        int exitCode = cmd.execute(schema, plan);
        assertEquals(0, exitCode);
    }

    private String getFilePathForClassPathResource(String resource) throws URISyntaxException {
        return Path.of(ClassLoader.getSystemResource(resource).toURI()).toString();
    }
}
