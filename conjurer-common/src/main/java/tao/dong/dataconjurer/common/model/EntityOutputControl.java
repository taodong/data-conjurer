package tao.dong.dataconjurer.common.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public record EntityOutputControl(String name, Set<PropertyOutputControl> properties) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityOutputControl eoc) {
            return StringUtils.equals(name, eoc.name());
        }
        return false;
    }
}
