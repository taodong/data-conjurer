package tao.dong.dataconjurer.shell.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import tao.dong.dataconjurer.common.model.DataSchema;

import java.io.IOException;
import java.io.InputStream;

@Service
public class YamlFileService {

    private final ObjectMapper yamlMapper;

    public YamlFileService(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    public DataSchema parseSchemaFile(String content) throws IOException {
        return yamlMapper.readValue(content, DataSchema.class);
    }
}
