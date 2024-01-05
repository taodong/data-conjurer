package tao.dong.dataconjurer.common.service;

import tao.dong.dataconjurer.common.model.DataProviderType;
import tao.dong.dataconjurer.common.support.CategorizedValueProvider;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;

import java.util.Optional;
import java.util.function.Function;

public interface DataProviderService {
    CategorizedValueProvider getValueProviderByType(String providerType);
    CharacterGroupLookup getCharacterGroupLookup();
    String getName();

    default boolean isProviderSupported(Function<String, DataProviderType> supportCheckFun, String providerType) {
        return Optional.ofNullable(supportCheckFun.apply(providerType)).isPresent();
    }
}
