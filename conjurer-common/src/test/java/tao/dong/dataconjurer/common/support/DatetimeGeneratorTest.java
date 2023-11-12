package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Duration;
import tao.dong.dataconjurer.common.model.SecondMark;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatetimeGeneratorTest {

    @Test
    void testGenerate() {
        var start = new SecondMark();
        start.setYear(2023);
        start.setMonth(11);
        start.setDay(10);
        var end = new SecondMark();
        end.setYear(2023);
        end.setMonth(11);
        end.setDay(11);
        Set<Constraint<?>> constraints = Set.of(new Duration(start, end));
        var generator = new DatetimeGenerator(constraints);
        for (var i = 0; i < 10; i++) {
            var rs = generator.generate();
            assertTrue(1699574400456L <= rs && 1699660800469L > rs, "Generated value " + rs + " isn't between 1699574400456 and 1699660800469");
        }
    }
}
