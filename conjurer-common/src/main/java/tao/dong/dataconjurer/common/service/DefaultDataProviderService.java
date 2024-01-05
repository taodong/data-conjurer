package tao.dong.dataconjurer.common.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import tao.dong.dataconjurer.common.model.ConjurerDataProviderType;
import tao.dong.dataconjurer.common.model.DataProviderType;
import tao.dong.dataconjurer.common.support.CategorizedValueProvider;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class DefaultDataProviderService implements DataProviderService {
    private static final Function<String, DataProviderType> CHECK_FUN = ConjurerDataProviderType::getByTypeName;

    @Getter
    private final CharacterGroupLookup characterGroupLookup;
    private final Map<String, CategorizedValueProvider> supportedProviders = new HashMap<>();

    public DefaultDataProviderService(@NotNull CharacterGroupLookup characterGroupLookup, @NotNull CategorizedValueProvider... providers) {
        this.characterGroupLookup = characterGroupLookup;
        supportedProviders.putAll(
            Arrays.stream(providers).collect(toMap(p -> p.getDataProviderType().name(), Function.identity()))
        );
    }

    @Override
    public CategorizedValueProvider getValueProviderByType(String providerType) {
        if (isProviderSupported(CHECK_FUN, providerType)) {
            var provider = supportedProviders.get(CHECK_FUN.apply(providerType).name());
            if (provider != null) {
                return provider;
            }
        }
        throw new UnsupportedOperationException("Data provider " + providerType + " isn't supported");
    }

    @Override
    public String getName() {
        return "Default Data Provider Service";
    }
}
