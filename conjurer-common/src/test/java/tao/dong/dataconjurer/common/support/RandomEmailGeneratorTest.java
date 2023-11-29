package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomEmailGeneratorTest {
    private static final Pattern EMAIL_REG = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    private final RandomEmailGenerator generator = new RandomEmailGenerator();

    @Test
    void testGenerate() {
        for (var i = 0; i < 10; i++) {
            var email = generator.generate();
            assertTrue(EMAIL_REG.matcher(email).matches(), "Generated email value " + email + " doesn't match the pattern");
        }
    }

}
