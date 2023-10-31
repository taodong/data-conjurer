package tao.dong.dataconjurer.engine.database.service;

import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DataGenerateService {

    private final DataGenerateConfig config;

    public DataGenerateService(DataGenerateConfig config) {
        this.config = config;
    }

    public void generateData(@NotEmpty Set<EntityWrapper> entities) {
        final var entityMap = new ConcurrentHashMap<>(
          entities.stream().collect(Collectors.toMap(EntityWrapper::getEntityName, Function.identity()))
        );
        final var latch = new CountDownLatch(entities.size());
        var pool = Executors.newFixedThreadPool(config.getHandlerCount());
        try {
            latch.await(config.getTimeOut().getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Data generation is interrupted", e);
            Thread.currentThread().interrupt();
        }
        pool.shutdown();
    }
}
