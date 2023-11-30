package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultEmailProviderTest {
    private final DefaultEmailProvider provider = new DefaultEmailProvider();

    @Test
    void testFetch() {
        var rs = provider.fetch(15, null, null, null);
        assertEquals(15, rs.size());
    }


}
