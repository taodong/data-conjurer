package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAddressProviderTest {
    private final DefaultAddressProvider provider = new DefaultAddressProvider();

    @Test
    void testFetch() {
        assertEquals(9, provider.fetch(9, Locale.JAPAN, Map.of("street", List.of())).size());
    }

}
