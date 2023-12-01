package tao.dong.dataconjurer.common.service;

import lombok.Builder;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.DataProviderType;
import tao.dong.dataconjurer.common.support.CategorizedStringValueProvider;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;

@Builder
public class DefaultDataProviderService implements V1DataProviderApi {

    private final CharacterGroupLookup characterGroupLookup;
    private final CategorizedStringValueProvider emailProvider;
    private final CategorizedStringValueProvider nameProvider;
    private final CategorizedStringValueProvider addressProvider;

    public DefaultDataProviderService(CharacterGroupLookup characterGroupLookup, CategorizedStringValueProvider emailProvider,
                                      CategorizedStringValueProvider nameProvider, CategorizedStringValueProvider addressProvider) {
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
    public CategorizedStringValueProvider getEmailProvider() {
        return getSupportedProvider(this.emailProvider, "EmailProvider");
    }

    @Override
    public CategorizedStringValueProvider getNameProvider() {
        return getSupportedProvider(this.nameProvider, "NameProvider");
    }

    @Override
    public CategorizedStringValueProvider getAddressProvider() {
        return getSupportedProvider(this.addressProvider, "AddressProvider");
    }


}
