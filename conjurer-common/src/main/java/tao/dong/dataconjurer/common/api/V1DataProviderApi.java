package tao.dong.dataconjurer.common.api;

import tao.dong.dataconjurer.common.support.CategorizedValueProvider;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;

import java.util.Optional;

public interface V1DataProviderApi {

    CharacterGroupLookup getCharacterGroupLookup();

    CategorizedValueProvider getEmailProvider();

    CategorizedValueProvider getNameProvider();

    CategorizedValueProvider getAddressProvider();

    default <T> T getSupportedProvider(T provider, String providerName) {
        return Optional.ofNullable(provider).orElseThrow(() -> new UnsupportedOperationException(providerName + " isn't supported"));
    }

    default String getApiVersion() {
        return "v1.0";
    }
}
