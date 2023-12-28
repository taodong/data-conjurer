package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainedBigDecimalGeneratorTest {

    @Test
    void testGenerate() {
        var generator = new ChainedBigDecimalGenerator(1, 2.0, 2, BigDecimal.valueOf(5.0));
        var rs = generator.generate();
        assertEquals(BigDecimal.valueOf(5.0), rs);
        rs = generator.generate();
        assertTrue(rs.compareTo(BigDecimal.valueOf(5.0)) >= 0 && rs.compareTo(BigDecimal.valueOf(9.0)) <= 0);
    }
}
