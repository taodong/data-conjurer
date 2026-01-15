package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tao.dong.dataconjurer.common.model.ChainedValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Duration;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.SecondMark;
import tao.dong.dataconjurer.common.model.TimeLeap;
import tao.dong.dataconjurer.common.model.TimeSpan;

import java.text.ParseException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.model.DefaultStringValueFormat.DATETIME_FORMAT;

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
        Set<Constraint<?>> constraints = Set.of(new Length(5L), new Duration(start, end), new Duration(start, end));
        var generator = new DatetimeGenerator(constraints);
        for (var i = 0; i < 10; i++) {
            var rs = generator.generate();
            assertTrue(1699574400456L <= rs && 1699660800469L > rs, "Generated value " + rs + " isn't between 1699574400456 and 1699660800469");
        }
    }

    @Test
    void testGenerateTimeSpanOnly() throws ParseException {
        var leap = new TimeLeap();
        leap.setDays(-2);
        var anchor = DataHelper.convertFormattedStringToMillisecond("2026-1-15 0:0:0", DATETIME_FORMAT.getFormat());
        var span = new TimeSpan("2026-01-15 00:00:00", leap);
        Set<Constraint<?>> constraints = Set.of(span);
        var generator = new DatetimeGenerator(constraints);
        for (var i = 0; i < 10; i++) {
            var rs = generator.generate();
            assertTrue(rs <= anchor && rs >= anchor - 2 * 86400000, "Generated value " + rs + " isn't between 2026-01-13 and 2026-01-15");
        }
    }

    @Test
    void testGenerateDefault() {
        var generator = new DatetimeGenerator(null);
        assertNotNull(generator.getGenerator());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1})
    void testChainedGenerator(int direction) {
        Set<Constraint<?>> constraints = Set.of(new ChainedValue(10.0, direction, 1));
        var generator = new DatetimeGenerator(constraints);
        assertInstanceOf(ChainedLongGenerator.class, generator.getGenerator());
    }

}
