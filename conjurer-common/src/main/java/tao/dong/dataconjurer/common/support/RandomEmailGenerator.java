package tao.dong.dataconjurer.common.support;

import java.util.Set;

public class RandomEmailGenerator {
    private final RandomStringValueGenerator partGenerator = new RangeLengthStringGenerator(3, 8);
    private final ElectedValueSelector domainTypeGenerator = new ElectedValueSelector(Set.of("com", "info", "org", "net", "inc", "app", "ai", "io"));

    public String generate() {
        return partGenerator.generate() + '.' + partGenerator.generate() + '@' + partGenerator.generate() + '.' + domainTypeGenerator.generate();
    }

}
