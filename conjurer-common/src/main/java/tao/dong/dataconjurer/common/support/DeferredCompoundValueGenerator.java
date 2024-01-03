package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ReferenceStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static tao.dong.dataconjurer.common.support.CompoundValuePropertyRetriever.DEFAULT_QUALIFIER;


public class DeferredCompoundValueGenerator implements ValueGenerator<CompoundValue> {

    private final CategorizedValueProvider provider;
    private final int recordSize;
    private final Locale locale;
    private final Map<String, List<Constraint<?>>> qualifierConstraints = new HashMap<>();
    private ElectedValueSelector generator;

    public DeferredCompoundValueGenerator(CategorizedValueProvider provider, int recordSize, Locale locale) {
        this.provider = provider;
        this.recordSize = recordSize;
        this.locale = locale;
    }

    public void addConstraints(String qualifier, List<Constraint<?>> constraints) {
        qualifierConstraints.put(qualifier == null ? DEFAULT_QUALIFIER : qualifier, constraints == null ? Collections.emptyList() : constraints);
    }

    @Override
    public CompoundValue generate() {
        return (CompoundValue) computeGenerator().generate();
    }

    private ElectedValueSelector computeGenerator() {
        if (generator == null) {
            var values = provider.fetch(recordSize, locale, qualifierConstraints);
            generator = new ElectedValueSelector(values, ReferenceStrategy.LOOP);
        }

        return generator;
    }
}
