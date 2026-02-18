package tao.dong.dataconjurer.shell.service;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataPlan;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.model.Dialect;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.PropertyType;
import tao.dong.dataconjurer.shell.model.MySQLDataPlan;
import tao.dong.dataconjurer.shell.support.AppConfig;
import tao.dong.dataconjurer.shell.support.DataGenerationProperties;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {YamlFileService.class, DataGenerationProperties.class})
@Import(AppConfig.class)
class YamlFileServiceTest {

    @SuppressWarnings("unused")
    @Autowired
    private YamlFileService yamlFileService;

    @SuppressWarnings("unused")
    @Autowired
    private YAMLMapper yamlMapper;

    @Test
    void testParseSchemaFile() throws IOException {
        DataSchema expected = new DataSchema("schema1", Set.of(new DataEntity("table1", Set.of(new EntityProperty("col1", PropertyType.SEQUENCE, null, null, null)))));
        var content = yamlMapper.writerFor(DataSchema.class).writeValueAsString(expected);
        var rs = yamlFileService.parseSchemaFile(content);
        assertEquals(expected, rs);
    }

    @Test
    void testParsePlanFile() throws IOException {
        MySQLDataPlan expected = new MySQLDataPlan();
        expected.setPlan(new DataPlan("test", "test", Dialect.MYSQL, List.of(
                new EntityData("entity1", 0, 1L, null, null)
        )));
        var content = yamlMapper.writerFor(MySQLDataPlan.class).writeValueAsString(expected);
        var rs = yamlFileService.parsePlanFile(content);
        assertEquals(expected, rs);
    }
}
