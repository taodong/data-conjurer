package tao.dong.dataconjurer.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class TypedValue {

    protected final PropertyType type;

    public abstract DataType getDataType();
    public abstract void join(TypedValue tv);
    public abstract void clearOrderedValues();
    public abstract List<Object> getOrderedValues();

    public enum DataType {
        SIMPLE,LINKED
    }

    protected void validateJoin(PropertyType type, DataType dataType) {
        if (this.type != type || this.getDataType() != dataType) {
            throw new IllegalArgumentException(
                    "Type mismatched values aren't allowed to join: %s|%s vs %s|%s".formatted(this.type.getName(), getDataType().name(), type.getName(), dataType.name())
            );
        }
    }

}
