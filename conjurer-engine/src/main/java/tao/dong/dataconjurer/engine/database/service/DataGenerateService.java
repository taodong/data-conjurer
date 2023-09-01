package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.EntityWrapper;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DataGenerateService {

    private final int handlerCount;
    private final Duration timeOut;


    public DataGenerateService(@Min(1) int handlerCount, @NotNull Duration timeOut) {
        this.handlerCount = handlerCount;
        this.timeOut = timeOut;
    }

    public void generateData(@NotEmpty Set<EntityWrapper> entities) {
        final var entityMap = new ConcurrentHashMap<>(
          entities.stream().collect(Collectors.toMap(EntityWrapper::getEntityName, Function.identity()))
        );
        final var latch = new CountDownLatch(entities.size());
        var pool = Executors.newFixedThreadPool(handlerCount);
        try {
            latch.await(timeOut.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Data generation is interrupted", e);
            Thread.currentThread().interrupt();
        }
        pool.shutdown();
    }
}
