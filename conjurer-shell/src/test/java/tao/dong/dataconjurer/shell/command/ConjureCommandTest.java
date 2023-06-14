package tao.dong.dataconjurer.shell.command;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import tao.dong.dataconjurer.shell.service.YamlFileService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ConjureCommandTest {

    private final PrintWriter printWriter = new PrintWriter(new StringWriter());

    @Test
    void testConjureCommand() throws URISyntaxException {
        YamlFileService yamlFileService = mock(YamlFileService.class);

        var conjureCommand = new ConjureCommand(yamlFileService);
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
