package tao.dong.dataconjurer.shell.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "data")
@Data
public class DataGenerationProperties {
    private int handlerCount;
    private Duration entityGenTimeOut;
    private int maxIndexCollision;
    private boolean partialResult;
    private Duration dataGenCheckInterval;
    private Duration dataGenTimeOut;
}
