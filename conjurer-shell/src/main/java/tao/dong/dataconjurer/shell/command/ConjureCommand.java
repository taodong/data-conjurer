package tao.dong.dataconjurer.shell.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.Dialect;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.engine.database.service.SqlService;
import tao.dong.dataconjurer.shell.service.FileOutputService;
import tao.dong.dataconjurer.shell.service.YamlFileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static picocli.CommandLine.ExitCode.OK;
import static picocli.CommandLine.ExitCode.USAGE;

@Component
@Command(name = "conjure", mixinStandardHelpOptions = true, version = "checksum 4.0",
                     description = "Command to generate data")
@Slf4j
@Getter(AccessLevel.PACKAGE)
public class ConjureCommand  implements Callable<Integer> {

    @SuppressWarnings("unused")
    @Parameters(index = "0", description = "Data schema file")
    private File schema;
    @SuppressWarnings("unused")
    @Parameters(index = "1", description = "Data generation plan")
    private File plan;

    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-e", "--entity-timeout"}, description = "Single entity generation timeout in minutes")
    private Integer timeOutInMinutes;
    @SuppressWarnings("unused")
    @CommandLine.Option(names= {"-c", "--max-collision"}, description = "Max occurrence of generated records which violate index constraints")
    private Integer maxCollision;
    @SuppressWarnings("unused")
    @CommandLine.Option(names= {"-i", "--wait-interval"}, description = "Wait interval of data generation service to check entity status updates in seconds")
    private Integer generationInterval;
    @SuppressWarnings("unused")
    @CommandLine.Option(names = {"-t", "--timeout"}, description = "Program execution timeout in minutes")
    private Integer maxTimeout;
    @SuppressWarnings("unused")
    @CommandLine.Option(names={"-p", "--partial-result"}, description = "Allow partial results of entity generation")
    private Boolean partialResult;

    private final YamlFileService yamlFileService;
    private final Validator validator;
    private final SqlService sqlService;
    private final DataGenerateConfig dataGenerateConfig;
    private final FileOutputService fileOutputService;

    public ConjureCommand(YamlFileService yamlFileService, Validator validator, SqlService sqlService, DataGenerateConfig dataGenerateConfig, FileOutputService fileOutputService) {
        this.yamlFileService = yamlFileService;
        this.validator = validator;
        this.sqlService = sqlService;
        this.dataGenerateConfig = dataGenerateConfig;
        this.fileOutputService = fileOutputService;
    }

    @Override
    public Integer call() {
        System.out.println("Start data generating");
        var exitCode = OK;
        var start = Instant.now();
        try {
            var schemaPath = this.schema.toPath();
            var schemaYaml = Files.readString(schemaPath, StandardCharsets.UTF_8);

            LOG.info("Parsing schema file: {}", schemaPath);
            var dataSchema = yamlFileService.parseSchemaFile(schemaYaml);
            validateInput(dataSchema, "schema", schemaPath);

            var planPath = this.plan.toPath();
            var planYaml = Files.readString(planPath, StandardCharsets.UTF_8);
            LOG.info("Parsing plan file: {}", planPath);
            var mysqlPlan = yamlFileService.parsePlanFile(planYaml);
            var rawPlan = mysqlPlan.getPlan();
            var outputControl = mysqlPlan.getOutput();
            var dataPlan = setDefaultDataPlanValuesForMySQL(rawPlan);
            validateInput(dataPlan, "plan", planPath);
            var generated = sqlService.generateSQLs(dataSchema, updatedConfig(), outputControl, dataPlan);
            fileOutputService.generateSQLFiles(generated);
        } catch (IOException ioe) {
            LOG.error("Invalid input", ioe);
            exitCode = USAGE;
        }
        var end = Instant.now();
        LOG.info("Generation completed in {} seconds with exit code {}", Duration.between(start, end).getSeconds(), exitCode);
        System.out.println("Data generation completed");
        return exitCode;
    }

    private DataGenerateConfig updatedConfig() {
        return DataGenerateConfig.builder()
                .dataGenTimeOut(maxTimeout == null ? dataGenerateConfig.getDataGenTimeOut() : Duration.ofMinutes(maxTimeout))
                .maxIndexCollision(maxCollision == null ? dataGenerateConfig.getMaxIndexCollision() : maxCollision)
                .dataGenCheckInterval(generationInterval == null? dataGenerateConfig.getDataGenCheckInterval() : Duration.ofSeconds(generationInterval))
                .entityGenTimeOut(timeOutInMinutes == null ? dataGenerateConfig.getEntityGenTimeOut() : Duration.ofMinutes(timeOutInMinutes))
                .partialResult(partialResult == null ? dataGenerateConfig.isPartialResult() : partialResult)
                .build();
    }

    private <T> void validateInput(T input, String name, Path filePath) {
        var violations = validator.validate(input);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new CommandLine.TypeConversionException(generateErrorMessage("Invalid " + name + " file: " + filePath, violations));
        }
    }

    private <T> String generateErrorMessage(String errMsg, Set<ConstraintViolation<T>> violations) {
        var msg = new StringBuilder(errMsg + '\n');
        var details = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
        msg.append(details);
        return msg.toString();
    }

    private DataPlan setDefaultDataPlanValuesForMySQL(DataPlan raw) {
        return new DataPlan(raw.name(), raw.schema(), Dialect.MYSQL, raw.data());
    }
}
