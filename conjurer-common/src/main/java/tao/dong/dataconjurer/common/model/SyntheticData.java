package tao.dong.dataconjurer.common.model;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.concurrent.atomic.AtomicInteger;


public class SyntheticData {

    private final ListOrderedMap<String, EntityWrapper> entities = new ListOrderedMap<>();
    private final AtomicInteger processed = new AtomicInteger(0);

    public EntityWrapper getUnprocessed() {
        // ToDo ...
        return null;
    }

    public boolean isCompleted() {
        return processed.get() == entities.size();
    }

}
