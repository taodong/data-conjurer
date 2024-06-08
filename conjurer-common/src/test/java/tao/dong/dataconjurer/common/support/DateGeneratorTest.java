package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.ChainedValue;
import tao.dong.dataconjurer.common.model.Duration;
import tao.dong.dataconjurer.common.model.SecondMark;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class DateGeneratorTest {

    @Test
    void calculateSeed() {
        DateGenerator dateGenerator = mock(DateGenerator.class, CALLS_REAL_METHODS);
        var result = dateGenerator.calculateSeed(1.0);
        assertEquals(86400000.0, result);
    }

    @Test
    void testGenerateChainValues() {
        var start = new SecondMark();
        start.setYear(2020);
        var end = new SecondMark();
        end.setYear(2021);
        DateGenerator dateGenerator = new DateGenerator(Set.of(
                new ChainedValue(1.0, 1, 0),
                new Duration(start, end)
        ));
        var initial = dateGenerator.generate();
        Long next = null;
        for (int i = 0; i < 5; i++) {
            next = dateGenerator.generate();
        }
        assertEquals(5, (next - initial) / 86400000);

    }

}