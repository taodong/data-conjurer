package tao.dong.dataconjurer.shell.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import tao.dong.dataconjurer.shell.service.YamlFileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
    private final Validator validator;

    public ConjureCommand(YamlFileService yamlFileService, Validator validator) {
        this.yamlFileService = yamlFileService;
        this.validator = validator;
    }

    @Override
    public Integer call() {
        var exitCode = OK;
        try {
            var schemaYaml = Files.readString(this.schema.toPath(), StandardCharsets.UTF_8);

            var dataSchema = yamlFileService.parseSchemaFile(schemaYaml);
            LOG.info("Schema: {}", dataSchema);
            var violations = validator.validate(dataSchema);
            if (!CollectionUtils.isEmpty(violations)) {
                throw new CommandLine.TypeConversionException(generateErrorMessage("Invalid schema file: " + this.schema.toPath(), violations));
            }

            var planYaml = Files.readString(this.plan.toPath(), StandardCharsets.UTF_8);
            var dataPlan = yamlFileService.parsePlanFile(planYaml);
            LOG.info("Plan: {}", dataPlan);
        } catch (IOException ioe) {
            LOG.error("Invalid input", ioe);
            exitCode = USAGE;
        }
        return exitCode;
    }

    private <T> String generateErrorMessage(String errMsg, Set<ConstraintViolation<T>> violations) {
        var msg = new StringBuilder(errMsg + '\n');
        var details = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
        msg.append(details);
        return msg.toString();
    }
}
