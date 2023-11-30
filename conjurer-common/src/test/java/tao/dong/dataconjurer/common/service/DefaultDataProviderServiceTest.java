package tao.dong.dataconjurer.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import tao.dong.dataconjurer.common.model.DataProviderType;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;
import tao.dong.dataconjurer.common.support.DefaultNameProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DefaultDataProviderServiceTest {

    @Test
    void testVersion() {
        var service = DefaultDataProviderService.builder().build();
        assertEquals("v1.0", service.getApiVersion());
    }

    @Test
    void testUnsupportedProvider() {
        var service = DefaultDataProviderService.builder().build();
        assertThrows(UnsupportedOperationException.class, service::getCharacterGroupLookup);
    }

    @Test
    void testSupportedProvider() {
        CharacterGroupLookup characterGroupLookup = mock(CharacterGroupLookup.class);
        var service = DefaultDataProviderService.builder().characterGroupLookup(characterGroupLookup).build();
        assertNotNull(service.getCharacterGroupLookup());
    }

    @ParameterizedTest
    @EnumSource(DataProviderType.class)
    void testGetDataProvider_NoMatch(DataProviderType type) {
        var service = DefaultDataProviderService.builder().build();
        assertThrows(UnsupportedOperationException.class, () -> service.getDataProvider(type));
    }

}
