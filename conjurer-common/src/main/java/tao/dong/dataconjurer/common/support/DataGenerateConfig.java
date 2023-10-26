package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Builder
@Data
public class DataGenerateConfig {
    @Builder.Default
    private int handlerCount = 1; // Threads to generate data
    @Builder.Default
    private Duration timeOut = Duration.ofMinutes(10); // Data generation timeout
    @Builder.Default
    private int maxIndexCollision = 100; // Max occurrence of generated records has none unique index per entity
    @Builder.Default
    private boolean partialResult = true; // If return generated data when operation failed
}
