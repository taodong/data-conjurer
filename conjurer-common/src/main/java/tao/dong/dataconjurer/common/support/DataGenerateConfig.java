package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Builder
@Data
public class DataGenerateConfig {
    @Builder.Default
    private Duration entityGenTimeOut = Duration.ofMinutes(5); // Data generation timeout for single entity
    @Builder.Default
    private int maxIndexCollision = 100; // Max occurrence of generated records has none unique index per entity
    @Builder.Default
    private boolean partialResult = false; // If return generated data when operation failed
    @Builder.Default
    private Duration dataGenCheckInterval = Duration.ofSeconds(10); // Wait interval of data generation service to check entity status updates
    @Builder.Default
    private Duration dataGenTimeOut = Duration.ofMinutes(15); // Overall time out for data generating process
}
