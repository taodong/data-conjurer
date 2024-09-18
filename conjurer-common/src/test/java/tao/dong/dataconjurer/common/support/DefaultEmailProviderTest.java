package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.Email;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultEmailProviderTest {
    private final DefaultEmailProvider provider = new DefaultEmailProvider();

    @Test
    void testFetch() {
        var rs = provider.fetch(15, null, null);
        assertEquals(15, rs.size());
        for (var i = 0; i < 15; i++) {
            var email = ((Email)rs.get(i)).value();
            assertEquals(StringUtils.lowerCase(email), email);
        }
    }


}
