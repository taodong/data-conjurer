package tao.dong.dataconjurer.common.api;

import java.util.Optional;

public interface V1DataProviderApi {

    CharacterGroupLookup getCharacterGroupLookup();

    CategorizedStringValueProvider getEmailProvider();

    CategorizedStringValueProvider getNameProvider();

    CategorizedStringValueProvider getAddressProvider();

    default <T> T getSupportedProvider(T provider, String providerName) {
        return Optional.ofNullable(provider).orElseThrow(() -> new UnsupportedOperationException(providerName + " isn't supported"));
    }

    default String getApiVersion() {
        return "v1.0";
    }
}
