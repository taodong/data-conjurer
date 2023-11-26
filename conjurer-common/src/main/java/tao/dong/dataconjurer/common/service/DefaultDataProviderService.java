package tao.dong.dataconjurer.common.service;

import lombok.Builder;
import tao.dong.dataconjurer.common.api.CharacterGroupLookup;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;

@Builder
public class DefaultDataProviderService implements V1DataProviderApi {

    private final CharacterGroupLookup characterGroupLookup;

    public DefaultDataProviderService(CharacterGroupLookup characterGroupLookup) {
        this.characterGroupLookup = characterGroupLookup;
    }


    @Override
    public CharacterGroupLookup getCharacterGroupLookup() {
        return getSupportedProvider(this.characterGroupLookup, "CharacterGroupLookup");
    }


}
