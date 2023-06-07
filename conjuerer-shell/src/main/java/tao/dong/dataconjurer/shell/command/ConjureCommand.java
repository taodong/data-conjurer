package tao.dong.dataconjurer.shell.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import tao.dong.dataconjurer.shell.service.YamlFileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;
import static picocli.CommandLine.ExitCode.USAGE;

@Component
@Command(name = "conjure", mixinStandardHelpOptions = true, version = "checksum 4.0",
                     description = "Command to generate data")
@Slf4j
public class ConjureCommand  implements Callable<Integer> {

    @Parameters(index = "0", description = "Data schema file")
    private File schema;
    @Parameters(index = "1", description = "Data generation plan")
    private File plan;

    private final YamlFileService yamlFileService;

    public ConjureCommand(YamlFileService yamlFileService) {
        this.yamlFileService = yamlFileService;
    }

    @Override
    public Integer call() {
        var exitCode = OK;
        try {
            var schemaYaml = Files.readString(this.schema.toPath(), StandardCharsets.UTF_8);

            var schema = yamlFileService.parseSchemaFile(schemaYaml);
            LOG.info("Schema: {}", schema);

            var dataPlan = Files.readString(this.plan.toPath(), StandardCharsets.UTF_8);
            System.out.println(dataPlan);
        } catch (IOException ioe) {
            LOG.error("Invalid input", ioe);
            exitCode = USAGE;
        }
        return exitCode;
    }
}
