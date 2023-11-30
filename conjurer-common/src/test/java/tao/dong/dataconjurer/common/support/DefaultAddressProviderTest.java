package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAddressProviderTest {
    private final DefaultAddressProvider provider = new DefaultAddressProvider();

    @Test
    void testFetch() {
        assertEquals(9, provider.fetch(9, "street", Locale.JAPAN, null).size());
    }

}
