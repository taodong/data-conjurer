package tao.dong.dataconjurer.shell.command;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConjureCommandTest {

    private final PrintWriter printWriter = new PrintWriter(new StringWriter());

    @Test
    void testConjureCommand() throws URISyntaxException {
        var conjureCommand = new ConjureCommand();
        var cmd = new CommandLine(conjureCommand);
        cmd.setOut(new PrintWriter(printWriter));

        String schema = getFilePathForClassPathResource("schema.toml");
        String plan = getFilePathForClassPathResource("plan.toml");

        int exitCode = cmd.execute(schema, plan);
        assertEquals(0, exitCode);
    }

    private String getFilePathForClassPathResource(String resource) throws URISyntaxException {
        return Path.of(ClassLoader.getSystemResource(resource).toURI()).toString();
    }
}
