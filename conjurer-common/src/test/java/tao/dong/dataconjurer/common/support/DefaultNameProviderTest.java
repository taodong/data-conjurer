package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
class DefaultNameProviderTest {
    private final DefaultNameProvider provider = new DefaultNameProvider();

    @Test
    void testFetch() {
        assertEquals(7, provider.fetch(7, null, null).size());
    }

}
