package tao.dong.dataconjurer.shell.service;

import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.shell.model.MySQLDataPlan;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;

@Service
public class YamlFileService {

    private final YAMLMapper yamlMapper;

    public YamlFileService(YAMLMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    public DataSchema parseSchemaFile(String content) throws IOException {
        return yamlMapper.readValue(content, DataSchema.class);
    }

    public MySQLDataPlan parsePlanFile(String content) throws IOException {
        return yamlMapper.readValue(content, MySQLDataPlan.class);
    }
}
