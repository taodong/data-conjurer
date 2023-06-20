package tao.dong.dataconjurer.common.model;

public abstract class PropertyValue {
    private Object value;
    protected Class type;

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
