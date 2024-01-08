package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DataProviderTypeTest {

    @Test
    void testGtByTypeName() {
        assertThrows(UnsupportedOperationException.class, () -> DataProviderType.getByTypeName("abc"));
    }
}
