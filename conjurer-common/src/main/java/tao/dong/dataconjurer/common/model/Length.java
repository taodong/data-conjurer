package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("length")
@JsonIgnoreProperties({"min", "includeMin", "includeMax"})
public class Length extends ValueRange<Long> {

    public Length(@JsonProperty("max") Long max) {
        super(0L, max, false, true);
    }

    @Override
    public String toString() {
        return "Length{" + "max=" + max + '}';
    }
}
