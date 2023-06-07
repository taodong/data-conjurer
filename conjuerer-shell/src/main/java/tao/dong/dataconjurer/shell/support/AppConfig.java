package tao.dong.dataconjurer.shell.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper yamlMapper() {
        var objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper;
    }

}
