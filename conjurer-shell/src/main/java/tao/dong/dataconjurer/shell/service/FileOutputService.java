package tao.dong.dataconjurer.shell.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tao.dong.dataconjurer.engine.database.model.EntityQueryOutput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Service
@Slf4j
public class FileOutputService {

    public void generateSQLFiles(Collection<EntityQueryOutput> results) {
        for (var result : results) {
            try {
                var filePath = getOutputFilePath(result);
                LOG.info("Creating file {}", filePath.toAbsolutePath());
                Files.writeString(filePath, result.getQueries().toString(),
                        StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
            } catch (IOException e) {
                LOG.error("Failed to create output file for entity {}", result.getEntity(), e);
            }
        }
    }

    Path getOutputFilePath(EntityQueryOutput eqo) {
        return Paths.get(eqo.getOrder() + "_" + eqo.getEntity() + ".sql");
    }
}
