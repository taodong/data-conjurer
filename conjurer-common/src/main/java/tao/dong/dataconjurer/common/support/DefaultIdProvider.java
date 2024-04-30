package tao.dong.dataconjurer.common.support;

import org.apache.commons.collections4.MapUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.StringFormat;
import tao.dong.dataconjurer.common.model.TextId;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class DefaultIdProvider implements CategorizedValueProvider{

    private final JFakerValueProvider provider = new JFakerValueProvider();

    @Override
    public String getDataProviderType() {
        return "ID";
    }

    @Override
    public List<? extends CompoundValue> fetch(int count, @SuppressWarnings("unused") Locale locale, Map<String, List<Constraint<?>>> constraints) {
        String format = null;
        if (MapUtils.isNotEmpty(constraints)) {
            var cs = constraints.values().stream().findFirst().orElse(List.of());
            format = cs.stream().filter(c -> c.getType() == ConstraintType.FORMAT).findFirst().map(c -> ((StringFormat)c).format()).orElse(null);
        }
        return format == null ? generateUuid(count) : provider.generateExpressionValues(count, format);
    }

    private List<TextId> generateUuid(int count) {
        return IntStream.range(0, count).mapToObj(i -> new TextId(UUID.randomUUID().toString())).toList();
    }


}
