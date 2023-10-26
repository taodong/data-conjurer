package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.REFERENCE;

@Builder
public class DataGenerateTask implements Callable<EntityWrapper> {

    private final EntityWrapper entityWrapper;
    private final CountDownLatch countDownLatch;
    private final DataGenerateConfig config;
    @Builder.Default
    private Map<String, List<TypedValue>> references = new HashMap<>();
    @Builder.Default
    private Map<String, IndexValueGenerator> referenceIndexTracker = new HashMap<>();
    @Builder.Default
    private Map<String, Boolean> referenceReady = new HashMap<>();

    @Override
    public EntityWrapper call() throws Exception {
        try {
            var record = generateRecord();
            return null;
        } finally {
            countDownLatch.countDown();
        }
    }

    private List<Object> generateRecord() {

        List<Object> record = new ArrayList<>(entityWrapper.getProperties().size());
        for (String propertyName : entityWrapper.getProperties()) {
            Object val;
            if (entityWrapper.getReferences().containsKey(propertyName)) {
                var ready = referenceReady.computeIfAbsent(propertyName, this::isReferenceReady);
                if (!ready) {
                    throw new DataGenerateException(REFERENCE,
                                                    String.format("No reference is available for %s.%s", entityWrapper.getEntityName(), propertyName));
                }
                var indexGen = referenceIndexTracker.computeIfAbsent(propertyName, k -> new RandomIndexGenerator(references.get(k).size()));
                val = references.get(propertyName).get(indexGen.generate());
            } else {
                val = entityWrapper.getGenerators().get(propertyName).generate();
            }
            record.add(val);

        }

        return record;
    }

    private boolean isReferenceReady(String key) {
        return CollectionUtils.isNotEmpty(references.get(key));
    }

}
