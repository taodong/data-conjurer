package tao.dong.dataconjurer.common.model;

import org.apache.commons.lang3.StringUtils;

public enum ConjurerDataProviderType implements DataProviderType {
    NAME,ADDRESS,EMAIL,ID;

    public static DataProviderType getByTypeName(String type) {
       try {
           return ConjurerDataProviderType.valueOf(StringUtils.trim(StringUtils.upperCase(type)));
       } catch (IllegalArgumentException | NullPointerException e) {
           return null;
       }

    }
}
