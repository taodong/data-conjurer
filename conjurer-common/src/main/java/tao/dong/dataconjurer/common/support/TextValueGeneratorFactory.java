package tao.dong.dataconjurer.common.support;

import org.apache.commons.collections4.CollectionUtils;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.TextValue;

import java.util.List;
import java.util.Set;

public class TextValueGeneratorFactory extends ValueGeneratorFactory<String> {

    @Override
    protected ValueGenerator createValueGenerator(EntityProperty property, Set<String> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            switch (property.type()) {
                case TEXT -> {return null;}
                case SEQUENCE -> {return null;}
            }
        } else {
            return createElectionGenerator(candidates);
        }

        return null;
    }

    private SequenceGenerator createSequenceGenerator(List<Constraint> constraints) {
        return null;
    }

    private ValueGenerator createElectionGenerator(Set<String> candidates) {
        return null;
    }
}
