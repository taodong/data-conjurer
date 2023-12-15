package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DataGenerateConfigTest {

    @Test
    void testGenerate_Default() {
        var config = DataGenerateConfig.builder().build();
        assertEquals(5, config.getEntityGenTimeOut().toMinutes());
        assertEquals(100, config.getMaxIndexCollision());
        assertFalse(config.isPartialResult());
        assertEquals(10, config.getDataGenCheckInterval().toSeconds());
        assertEquals(15, config.getDataGenTimeOut().toMinutes());
    }
}
