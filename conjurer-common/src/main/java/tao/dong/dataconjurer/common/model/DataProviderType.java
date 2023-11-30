package tao.dong.dataconjurer.common.model;

import org.apache.commons.lang3.StringUtils;

public enum DataProviderType {
    NAME,ADDRESS,EMAIL,UNKNOWN;

    public static DataProviderType getByTypeName(String type) {
       try {
           return DataProviderType.valueOf(StringUtils.trim(StringUtils.upperCase(type)));
       } catch (IllegalArgumentException | NullPointerException e) {
           return UNKNOWN;
       }

    }
}
