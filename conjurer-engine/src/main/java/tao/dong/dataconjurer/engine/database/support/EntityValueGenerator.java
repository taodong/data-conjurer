package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.model.EntityWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class EntityValueGenerator implements Callable<EntityWrapper> {
    private final CountDownLatch latch;
    private EntityWrapper entity;

    public EntityValueGenerator(CountDownLatch latch, EntityWrapper entity) {
        this.latch = latch;
        this.entity = entity;
    }

    @Override
    public EntityWrapper call() throws Exception {
        latch.countDown();
        return entity;
    }
}
