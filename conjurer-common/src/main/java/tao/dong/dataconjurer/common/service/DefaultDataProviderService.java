package tao.dong.dataconjurer.common.service;

import lombok.Builder;
import tao.dong.dataconjurer.common.api.CategorizedStringValueProvider;
import tao.dong.dataconjurer.common.api.CharacterGroupLookup;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;

@Builder
public class DefaultDataProviderService implements V1DataProviderApi {

    private final CharacterGroupLookup characterGroupLookup;
    private final CategorizedStringValueProvider emailProvider;

    public DefaultDataProviderService(CharacterGroupLookup characterGroupLookup, CategorizedStringValueProvider emailProvider) {
        this.characterGroupLookup = characterGroupLookup;
        this.emailProvider = emailProvider;
    }


    @Override
    public CharacterGroupLookup getCharacterGroupLookup() {
        return getSupportedProvider(this.characterGroupLookup, "CharacterGroupLookup");
    }

    @Override
    public CategorizedStringValueProvider getEmailProvider() {
        return getSupportedProvider(this.emailProvider, "EmailProvider");
    }


}
