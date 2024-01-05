package tao.dong.dataconjurer.common.model;

public interface DataProviderType {
    static DataProviderType getByTypeName(String type) {
        throw new UnsupportedOperationException("Data provider type " +  type + " is not supported.");
    }

    String name();
}
