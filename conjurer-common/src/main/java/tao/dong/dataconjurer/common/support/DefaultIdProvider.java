package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.TextId;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static tao.dong.dataconjurer.common.support.CompoundValuePropertyRetriever.DEFAULT_QUALIFIER;

public class DefaultIdProvider implements CategorizedValueProvider{

    private final JFakerValueProvider provider = new JFakerValueProvider();

    @Override
    public String getDataProviderType() {
        return "ID";
    }

    @Override
    public List<? extends CompoundValue> fetch(int count, @SuppressWarnings("unused") Locale locale, Map<String, List<Constraint<?>>> constraints) {
        var qualifier = constraints.isEmpty() ? null : constraints.keySet().iterator().next();
        var isUuid = qualifier == null || StringUtils.equalsAnyIgnoreCase(qualifier, "uuid", DEFAULT_QUALIFIER);
        return isUuid ? generateUuid(count) : provider.generateExpressionValues(count, qualifier);
    }

    private List<TextId> generateUuid(int count) {
        return IntStream.range(0, count).mapToObj(i -> new TextId(UUID.randomUUID().toString())).toList();
    }


}
