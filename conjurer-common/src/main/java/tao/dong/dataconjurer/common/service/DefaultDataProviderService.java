package tao.dong.dataconjurer.common.service;

import lombok.Builder;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.support.CategorizedValueProvider;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;

@Builder
public class DefaultDataProviderService implements V1DataProviderApi {

    private final CharacterGroupLookup characterGroupLookup;
    private final CategorizedValueProvider emailProvider;
    private final CategorizedValueProvider nameProvider;
    private final CategorizedValueProvider addressProvider;

    public DefaultDataProviderService(CharacterGroupLookup characterGroupLookup, CategorizedValueProvider emailProvider,
                                      CategorizedValueProvider nameProvider, CategorizedValueProvider addressProvider) {
        this.characterGroupLookup = characterGroupLookup;
        this.emailProvider = emailProvider;
        this.nameProvider = nameProvider;
        this.addressProvider = addressProvider;
    }

    @Override
    public CharacterGroupLookup getCharacterGroupLookup() {
        return getSupportedProvider(this.characterGroupLookup, "CharacterGroupLookup");
    }

    @Override
    public CategorizedValueProvider getEmailProvider() {
        return getSupportedProvider(this.emailProvider, "EmailProvider");
    }

    @Override
    public CategorizedValueProvider getNameProvider() {
        return getSupportedProvider(this.nameProvider, "NameProvider");
    }

    @Override
    public CategorizedValueProvider getAddressProvider() {
        return getSupportedProvider(this.addressProvider, "AddressProvider");
    }


}
