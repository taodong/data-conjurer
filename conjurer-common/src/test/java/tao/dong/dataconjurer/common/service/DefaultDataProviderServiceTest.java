package tao.dong.dataconjurer.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tao.dong.dataconjurer.common.model.ConjurerDataProviderType;
import tao.dong.dataconjurer.common.support.CharacterGroupLookup;
import tao.dong.dataconjurer.common.support.DefaultEmailProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DefaultDataProviderServiceTest {

    @Test
    void testGetName() {
        DataProviderService providerService = new DefaultDataProviderService(mock(CharacterGroupLookup.class), new DefaultEmailProvider());
        assertEquals("Default Data Provider Service", providerService.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "Email", "EMAIL"})
    void testGetValueProviderByType(String typeName) {
        DataProviderService providerService = new DefaultDataProviderService(mock(CharacterGroupLookup.class), new DefaultEmailProvider());
        var provider = providerService.getValueProviderByType(typeName);
        assertEquals(ConjurerDataProviderType.EMAIL.name(), provider.getDataProviderType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "address", "abc"})
    void testGetValueProviderByType_Unsupported(String typeName) {
        DataProviderService providerService = new DefaultDataProviderService(mock(CharacterGroupLookup.class), new DefaultEmailProvider());
        assertThrows(UnsupportedOperationException.class, () -> providerService.getValueProviderByType(typeName));
    }
}
