package tao.dong.dataconjurer.common.model;

public record EntityWrapperId(String entityName, int dataId) {
    public String getIdString() {
        return entityName + "_" + dataId;
    }
}
